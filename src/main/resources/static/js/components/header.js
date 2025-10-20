/**
 * Header Component for Clinic Management System
 * Dynamically renders header based on user role and login state
 */

// Main function to render the header based on user role and current page
function renderHeader() {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return;

    // Clear session data on homepage
    if (window.location.pathname.endsWith("/") || window.location.pathname.includes("index.html")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
        headerDiv.innerHTML = `
            <header class="header">
                <div class="logo-section">
                    <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
                    <span class="logo-title">Hospital CMS</span>
                </div>
            </header>`;
        return;
    }

    // Get user role and token
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Handle invalid sessions
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    // Start building header content
    let headerContent = `
        <header class="header">
            <div class="logo-section">
                <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
                <span class="logo-title">Hospital CMS</span>
            </div>
            <nav class="nav-section">`;

    // Add role-specific content
    if (role === "admin") {
        headerContent += `
                <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
                <a href="#" class="nav-link" onclick="logout()">Logout</a>`;
    } else if (role === "doctor") {
        headerContent += `
                <button class="adminBtn" onclick="goToDashboard('doctor')">Home</button>
                <a href="#" class="nav-link" onclick="logout()">Logout</a>`;
    } else if (role === "patient") {
        headerContent += `
                <button id="patientLogin" class="adminBtn">Login</button>
                <button id="patientSignup" class="adminBtn">Sign Up</button>`;
    } else if (role === "loggedPatient") {
        headerContent += `
                <button id="home" class="adminBtn" onclick="goToDashboard('patient')">Home</button>
                <button id="patientAppointments" class="adminBtn" onclick="viewAppointments()">Appointments</button>
                <a href="#" class="nav-link" onclick="logoutPatient()">Logout</a>`;
    } else {
        // Default header for non-authenticated users on other pages
        headerContent += `
                <button class="adminBtn" onclick="goToHome()">Home</button>
                <button id="roleSelect" class="adminBtn" onclick="goToHome()">Select Role</button>`;
    }

    // Close header structure
    headerContent += `
            </nav>
        </header>`;

    // Inject header content
    headerDiv.innerHTML = headerContent;

    // Attach event listeners to dynamically created buttons
    attachHeaderButtonListeners();
}

// Attach event listeners to header buttons
function attachHeaderButtonListeners() {
    // Patient login button
    const patientLoginBtn = document.getElementById("patientLogin");
    if (patientLoginBtn) {
        patientLoginBtn.addEventListener("click", function() {
            openModal('patientLogin');
        });
    }

    // Patient signup button
    const patientSignupBtn = document.getElementById("patientSignup");
    if (patientSignupBtn) {
        patientSignupBtn.addEventListener("click", function() {
            openModal('patientSignup');
        });
    }

    // Add doctor button (for admin)
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", function() {
            openModal('addDoctor');
        });
    }

    // Home button for patient
    const homeBtn = document.getElementById("home");
    if (homeBtn) {
        homeBtn.addEventListener("click", function() {
            window.location.href = '/pages/patientDashboard.html';
        });
    }

    // Appointments button for patient
    const appointmentsBtn = document.getElementById("patientAppointments");
    if (appointmentsBtn) {
        appointmentsBtn.addEventListener("click", function() {
            window.location.href = '/pages/patientAppointments.html';
        });
    }
}

// Logout function for admin and doctor
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

// Logout function for patient (keeps patient role)
function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}

// Navigate to dashboard based on role
function goToDashboard(role) {
    if (role === 'doctor') {
        window.location.href = '/doctor/doctorDashboard.html';
    } else if (role === 'patient') {
        window.location.href = '/pages/patientDashboard.html';
    } else if (role === 'admin') {
        window.location.href = '/admin/adminDashboard.html';
    }
}

// Go to home page
function goToHome() {
    window.location.href = "/";
}

// View appointments for patient
function viewAppointments() {
    if (localStorage.getItem("userRole") === "loggedPatient") {
        window.location.href = '/pages/patientAppointments.html';
    } else {
        alert("Please log in to view appointments.");
        openModal('patientLogin');
    }
}

// Open modal function (to be implemented in modal service)
function openModal(modalType) {
    // This function will be implemented in the modal service
    console.log(`Opening modal: ${modalType}`);
    // Implementation will be added when modal service is created
    switch(modalType) {
        case 'patientLogin':
            // Show patient login modal
            showPatientLoginModal();
            break;
        case 'patientSignup':
            // Show patient signup modal
            showPatientSignupModal();
            break;
        case 'addDoctor':
            // Show add doctor modal
            showAddDoctorModal();
            break;
        default:
            console.log('Unknown modal type:', modalType);
    }
}

// Placeholder modal functions (to be implemented in modal service)
function showPatientLoginModal() {
    const modal = document.getElementById('modal');
    const modalBody = document.getElementById('modal-body');
    
    if (modal && modalBody) {
        modalBody.innerHTML = `
            <h3>Patient Login</h3>
            <form id="patientLoginForm">
                <div class="form-group">
                    <label for="patientEmail">Email:</label>
                    <input type="email" id="patientEmail" required>
                </div>
                <div class="form-group">
                    <label for="patientPassword">Password:</label>
                    <input type="password" id="patientPassword" required>
                </div>
                <button type="submit" class="confirm-btn">Login</button>
            </form>
        `;
        modal.style.display = 'block';
        
        // Add form submission handler
        const form = document.getElementById('patientLoginForm');
        if (form) {
            form.addEventListener('submit', handlePatientLogin);
        }
    }
}

function showPatientSignupModal() {
    const modal = document.getElementById('modal');
    const modalBody = document.getElementById('modal-body');
    
    if (modal && modalBody) {
        modalBody.innerHTML = `
            <h3>Patient Sign Up</h3>
            <form id="patientSignupForm">
                <div class="form-group">
                    <label for="signupName">Full Name:</label>
                    <input type="text" id="signupName" required>
                </div>
                <div class="form-group">
                    <label for="signupEmail">Email:</label>
                    <input type="email" id="signupEmail" required>
                </div>
                <div class="form-group">
                    <label for="signupPhone">Phone:</label>
                    <input type="tel" id="signupPhone" required>
                </div>
                <div class="form-group">
                    <label for="signupAddress">Address:</label>
                    <textarea id="signupAddress" required></textarea>
                </div>
                <div class="form-group">
                    <label for="signupPassword">Password:</label>
                    <input type="password" id="signupPassword" required>
                </div>
                <button type="submit" class="confirm-btn">Sign Up</button>
            </form>
        `;
        modal.style.display = 'block';
        
        // Add form submission handler
        const form = document.getElementById('patientSignupForm');
        if (form) {
            form.addEventListener('submit', handlePatientSignup);
        }
    }
}

function showAddDoctorModal() {
    const modal = document.getElementById('modal');
    const modalBody = document.getElementById('modal-body');
    
    if (modal && modalBody) {
        modalBody.innerHTML = `
            <h3>Add New Doctor</h3>
            <form id="addDoctorForm">
                <div class="form-group">
                    <label for="doctorName">Full Name:</label>
                    <input type="text" id="doctorName" required>
                </div>
                <div class="form-group">
                    <label for="doctorSpecialty">Specialty:</label>
                    <select id="doctorSpecialty" required>
                        <option value="">Select Specialty</option>
                        <option value="Cardiologist">Cardiologist</option>
                        <option value="Neurologist">Neurologist</option>
                        <option value="Orthopedist">Orthopedist</option>
                        <option value="Pediatrician">Pediatrician</option>
                        <option value="Dermatologist">Dermatologist</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="doctorEmail">Email:</label>
                    <input type="email" id="doctorEmail" required>
                </div>
                <div class="form-group">
                    <label for="doctorPhone">Phone:</label>
                    <input type="tel" id="doctorPhone" required>
                </div>
                <div class="form-group">
                    <label for="doctorPassword">Password:</label>
                    <input type="password" id="doctorPassword" required>
                </div>
                <button type="submit" class="confirm-btn">Add Doctor</button>
            </form>
        `;
        modal.style.display = 'block';
        
        // Add form submission handler
        const form = document.getElementById('addDoctorForm');
        if (form) {
            form.addEventListener('submit', handleAddDoctor);
        }
    }
}

// Placeholder form handlers (to be implemented with actual API calls)
function handlePatientLogin(event) {
    event.preventDefault();
    // Implement actual login logic here
    console.log('Patient login submitted');
    // For now, just simulate successful login
    localStorage.setItem('token', 'patient-token');
    localStorage.setItem('userRole', 'loggedPatient');
    document.getElementById('modal').style.display = 'none';
    window.location.reload();
}

function handlePatientSignup(event) {
    event.preventDefault();
    // Implement actual signup logic here
    console.log('Patient signup submitted');
    document.getElementById('modal').style.display = 'none';
    alert('Signup successful! Please login.');
}

function handleAddDoctor(event) {
    event.preventDefault();
    // Implement actual doctor addition logic here
    console.log('Add doctor submitted');
    document.getElementById('modal').style.display = 'none';
    alert('Doctor added successfully!');
    // Refresh the page or update doctor list
    window.location.reload();
}

// Close modal when clicking on X
document.addEventListener('DOMContentLoaded', function() {
    const closeModal = document.getElementById('closeModal');
    if (closeModal) {
        closeModal.addEventListener('click', function() {
            const modal = document.getElementById('modal');
            if (modal) {
                modal.style.display = 'none';
            }
        });
    }

    // Close modal when clicking outside
    window.addEventListener('click', function(event) {
        const modal = document.getElementById('modal');
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
});

// Initialize header when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    renderHeader();
});

// Export functions for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { renderHeader, logout, logoutPatient };
}