# User Story Template
**Title:** Admin Login
_As a admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. System validates admin credentials against database
2. Successful login redirects to admin dashboard
3. Failed login shows appropriate error message
4. Session is properly established upon successful login

**Priority:** High
**Story Points:** 3
**Notes:**
- Password should be encrypted
- Session timeout after 30 minutes of inactivity

---

# User Story Template
**Title:** Admin Logout
_As a admin, I want to log out of the portal, so that I can protect system access._

**Acceptance Criteria:**
1. Logout option is clearly visible in the navigation
2. Session is properly terminated upon logout
3. User is redirected to login page after logout
4. Cannot access protected routes after logout

**Priority:** High
**Story Points:** 2
**Notes:**
- Should clear all session data and cookies

---

# User Story Template
**Title:** Add Doctors
_As a admin, I want to add doctors to the portal, so that the healthcare system has sufficient medical professionals._

**Acceptance Criteria:**
1. Form to input doctor details (name, specialty, contact info, etc.)
2. System validates all required fields
3. Success message displayed upon successful addition
4. New doctor appears in the doctors list
5. Login credentials are generated for the new doctor

**Priority:** High
**Story Points:** 5
**Notes:**
- Should include email verification process
- Default password should be securely generated

---

# User Story Template
**Title:** Delete Doctor Profiles
_As a admin, I want to delete doctor's profile from the portal, so that I can maintain accurate and current staff records._

**Acceptance Criteria:**
1. Confirmation dialog before deletion
2. Doctor profile is completely removed from the system
3. Associated appointments are handled appropriately
4. Success message displayed upon deletion
5. System maintains referential integrity

**Priority:** Medium
**Story Points:** 4
**Notes:**
- Consider soft delete vs hard delete implementation
- May need to archive rather than completely remove

---

# User Story Template
**Title:** Appointment Statistics Report
_As a admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month, so that I can track usage statistics._

**Acceptance Criteria:**
1. Stored procedure executes successfully
2. Returns monthly appointment counts for specified date range
3. Results are formatted clearly in the CLI
4. Handles empty results gracefully
5. Procedure can filter by specific time periods

**Priority:** Medium
**Story Points:** 8
**Notes:**
- Procedure should be optimized for performance
- Consider creating a view for this data as well
- May want to extend to include other statistics in future
