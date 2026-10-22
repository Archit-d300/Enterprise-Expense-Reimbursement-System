package report;

public final class ReportRequest {
    private final int requesterEmployeeId;
    private final String requesterRole;
    private final String department;

    public ReportRequest(int requesterEmployeeId, String requesterRole, String department) {
        if (requesterEmployeeId <= 0) {
            throw new IllegalArgumentException("Requester employee ID must be greater than 0.");
        }
        if (requesterRole == null || requesterRole.trim().isEmpty()) {
            throw new IllegalArgumentException("Requester role is required.");
        }
        this.requesterEmployeeId = requesterEmployeeId;
        this.requesterRole = requesterRole.trim().toUpperCase(java.util.Locale.ROOT);
        this.department = department;
    }

    public int getRequesterEmployeeId() { return requesterEmployeeId; }
    public String getRequesterRole() { return requesterRole; }
    public String getDepartment() { return department; }
}
