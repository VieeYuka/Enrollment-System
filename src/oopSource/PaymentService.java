package oopSource;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Tuition records and payments.
 *
 * A student appears in the tuition list once their LATEST enrollment is approved (Enrolled).
 * Total due = units x TuitionFrame.RATE_PER_UNIT. A payment is saved in table "payments"
 * (one payment per enrollment).
 */
public final class PaymentService {

	private static final String SELECT_RECORD =
			"SELECT e.id AS enrollment_id, e.student_number, " + EnrollmentService.NAME_SQL + " AS student_name, "
			+ "s.course, s.year_level, e.total_units, "
			+ "p.payment_method, p.amount_received, p.change_amount, p.date_paid "
			+ "FROM enrollments e "
			+ "JOIN students s ON s.student_number = e.student_number "
			+ "LEFT JOIN payments p ON p.enrollment_id = e.id "
			+ "WHERE e.status = 'Enrolled' "
			+ "AND e.id = (SELECT MAX(e2.id) FROM enrollments e2 WHERE e2.student_number = e.student_number) ";

	private PaymentService() {
	}

	public static List<TuitionFrame.TuitionRecord> loadRecords() {

		List<TuitionFrame.TuitionRecord> list = new ArrayList<TuitionFrame.TuitionRecord>();

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(SELECT_RECORD + "ORDER BY e.id DESC");
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				list.add(map(rs));
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}
		return list;
	}

	public static TuitionFrame.TuitionRecord find(String studentId) {

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(SELECT_RECORD + "AND e.student_number = ?")) {

			pst.setString(1, studentId);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					return map(rs);
				}
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}
		return null;
	}

	
	public static boolean pay(TuitionFrame.TuitionRecord record, String method,
			double amountReceived, String receivedBy) {

		double due = record.getTotalDue();
		double change = amountReceived - due;

		String sql = "INSERT INTO payments (enrollment_id, amount_due, payment_method, "
				+ "amount_received, change_amount, date_paid, received_by) VALUES (?, ?, ?, ?, ?, ?, ?)";

		java.util.Date now = new java.util.Date();

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setInt(1, record.enrollmentId);
			pst.setBigDecimal(2, java.math.BigDecimal.valueOf(due));
			pst.setString(3, method);
			pst.setBigDecimal(4, java.math.BigDecimal.valueOf(amountReceived));
			pst.setBigDecimal(5, java.math.BigDecimal.valueOf(change));
			pst.setDate(6, new Date(now.getTime()));
			pst.setString(7, receivedBy);
			pst.executeUpdate();

		} catch (SQLException e) {

			if (e.getErrorCode() == 1062) {
				JOptionPane.showMessageDialog(null, "This tuition has already been paid.",
						"Already Paid", JOptionPane.INFORMATION_MESSAGE);
			} else {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Failed to save the payment:\n" + e.getMessage(),
						"Database Error", JOptionPane.ERROR_MESSAGE);
			}
			return false;
		}

		record.paid = true;
		record.paymentMethod = method;
		record.amountReceived = amountReceived;
		record.change = change;
		record.datePaid = new java.text.SimpleDateFormat("yyyy-MM-dd").format(now);
		return true;
	}
	private static boolean installment (TuitionFrame.TuitionRecord record, String method,double amountReceived, String receivedBy) {
		double due = record.getTotalDue();
		double updatedDue = amountReceived - due;
	    double downpayment = amountReceived;
		
		
		return true;
		
	}

	private static TuitionFrame.TuitionRecord map(ResultSet rs) throws SQLException {

		TuitionFrame.TuitionRecord r = new TuitionFrame.TuitionRecord(
				rs.getString("student_number"),
				rs.getString("student_name"),
				rs.getString("course"),
				rs.getString("year_level"),
				rs.getInt("total_units"));
		r.enrollmentId = rs.getInt("enrollment_id");

		Date paidOn = rs.getDate("date_paid");
		if (paidOn != null) {
			r.paid = true;
			r.paymentMethod = rs.getString("payment_method");
			r.amountReceived = rs.getDouble("amount_received");
			r.change = rs.getDouble("change_amount");
			r.datePaid = paidOn.toString();
		}
		return r;
	}
}
