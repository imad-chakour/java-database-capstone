/**
 * Modal Component for handling various modal types
 */

export function openModal(type, data = null) {
    let modalContent = '';
    
    if (type === 'addDoctor') {
        modalContent = `
            <div class="modal-form">
                <h2>Add Doctor</h2>
                <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field">
                <select id="specialization" class="input-field select-dropdown">
                    <option value="">Specialization</option>
                    <option value="Cardiologist">Cardiologist</option>
                    <option value="Dermatologist">Dermatologist</option>
                    <option value="Neurologist">Neurologist</option>
                    <option value="Pediatrician">Pediatrician</option>
                    <option value="Orthopedist">Orthopedist</option>
                    <option value="Gynecologist">Gynecologist</option>
                    <option value="Psychiatrist">Psychiatrist</option>
                    <option value="Dentist">Dentist</option>
                    <option value="Ophthalmologist">Ophthalmologist</option>
                    <option value="ENT Specialist">ENT Specialist</option>
                    <option value="Urologist">Urologist</option>
                    <option value="Oncologist">Oncologist</option>
                    <option value="Gastroenterologist">Gastroenterologist</option>
                    <option value="General Physician">General Physician</option>
                </select>
                <input type="email" id="doctorEmail" placeholder="Email" class="input-field">
                <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
                <input type="text" id="doctorPhone" placeholder="Mobile No." class="input-field">
                <div class="availability-container">
                    <label class="availabilityLabel">Select Availability:</label>
                    <div class="checkbox-group">
                        <label><input type="checkbox" name="availability" value="09:00-10:00"> 9:00 AM - 10:00 AM</label>
                        <label><input type="checkbox" name="availability" value="10:00-11:00"> 10:00 AM - 11:00 AM</label>
                        <label><input type="checkbox" name="availability" value="11:00-12:00"> 11:00 AM - 12:00 PM</label>
                        <label><input type="checkbox" name="availability" value="12:00-13:00"> 12:00 PM - 1:00 PM</label>
                        <label><input type="checkbox" name="availability" value="14:00-15:00"> 2:00 PM - 3:00 PM</label>
                        <label><input type="checkbox" name="availability" value="15:00-16:00"> 3:00 PM - 4:00 PM</label>
                        <label><input type="checkbox" name="availability" value="16:00-17:00"> 4:00 PM - 5:00 PM</label>
                    </div>
                </div>
                <button class="dashboard-btn" id="saveDoctorBtn">Save</button>
            </div>
        `;
    } else if (type === 'patientLogin') {
        modalContent = `
            <div class="modal-form">
                <h2>Patient Login</h2>
                <input type="email" id="patientEmail" placeholder="Email" class="input-field">
                <input type="password" id="patientPassword" placeholder="Password" class="input-field">
                <button class="dashboard-btn" id="loginBtn">Login</button>
            </div>
        `;
    } else if (type === "patientSignup") {
        modalContent = `
            <div class="modal-form">
                <h2>Patient Signup</h2>
                <input type="text" id="signupName" placeholder="Full Name" class="input-field">
                <input type="email" id="signupEmail" placeholder="Email" class="input-field">
                <input type="password" id="signupPassword" placeholder="Password" class="input-field">
                <input type="tel" id="signupPhone" placeholder="Phone" class="input-field">
                <textarea id="signupAddress" placeholder="Address" class="input-field textarea-field"></textarea>
                <button class="dashboard-btn" id="signupBtn">Signup</button>
            </div>
        `;
    } else if (type === 'adminLogin') {
        modalContent = `
            <div class="modal-form">
                <h2>Admin Login</h2>
                <input type="text" id="adminUsername" placeholder="Username" class="input-field">
                <input type="password" id="adminPassword" placeholder="Password" class="input-field">
                <button class="dashboard-btn" id="adminLoginBtn">Login</button>
            </div>
        `;
    } else if (type === 'doctorLogin') {
        modalContent = `
            <div class="modal-form">
                <h2>Doctor Login</h2>
                <input type="email" id="doctorEmail" placeholder="Email" class="input-field">
                <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
                <button class="dashboard-btn" id="doctorLoginBtn">Login</button>
            </div>
        `;
    } else if (type === 'editDoctor') {
        modalContent = `
            <div class="modal-form">
                <h2>Edit Doctor</h2>
                <input type="text" id="editDoctorName" placeholder="Doctor Name" class="input-field" value="${data?.name || ''}">
                <select id="editSpecialization" class="input-field select-dropdown">
                    <option value="">Specialization</option>
                    <option value="Cardiologist" ${data?.specialty === 'Cardiologist' ? 'selected' : ''}>Cardiologist</option>
                    <option value="Dermatologist" ${data?.specialty === 'Dermatologist' ? 'selected' : ''}>Dermatologist</option>
                    <option value="Neurologist" ${data?.specialty === 'Neurologist' ? 'selected' : ''}>Neurologist</option>
                    <option value="Pediatrician" ${data?.specialty === 'Pediatrician' ? 'selected' : ''}>Pediatrician</option>
                    <option value="Orthopedist" ${data?.specialty === 'Orthopedist' ? 'selected' : ''}>Orthopedist</option>
                </select>
                <input type="email" id="editDoctorEmail" placeholder="Email" class="input-field" value="${data?.email || ''}">
                <input type="text" id="editDoctorPhone" placeholder="Mobile No." class="input-field" value="${data?.phone || ''}">
                <button class="dashboard-btn" id="updateDoctorBtn">Update</button>
            </div>
        `;
    } else if (type === 'bookAppointment') {
        modalContent = `
            <div class="modal-form">
                <h2>Book Appointment with ${data?.doctorName || 'Doctor'}</h2>
                <p class="doctor-specialty">${data?.specialty || ''}</p>
                <input type="date" id="appointmentDate" class="input-field" min="${getTomorrowDate()}">
                <select id="appointmentTime" class="input-field select-dropdown">
                    <option value="">Select Time Slot</option>
                    ${generateTimeOptions(data?.availableTimes)}
                </select>
                <textarea id="symptoms" placeholder="Describe your symptoms or reason for visit..." class="input-field textarea-field"></textarea>
                <textarea id="notes" placeholder="Additional notes (optional)..." class="input-field textarea-field"></textarea>
                <button class="dashboard-btn" id="confirmBookingBtn">Confirm Booking</button>
            </div>
        `;
    }

    document.getElementById('modal-body').innerHTML = modalContent;
    document.getElementById('modal').style.display = 'block';

    // Attach event listeners based on modal type
    attachModalEventListeners(type, data);
}

function attachModalEventListeners(type, data) {
    // Close modal when clicking X
    document.getElementById('closeModal').onclick = () => {
        document.getElementById('modal').style.display = 'none';
    };

    // Close modal when clicking outside
    window.onclick = (event) => {
        const modal = document.getElementById('modal');
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };

    // Attach specific event listeners based on modal type
    switch(type) {
        case "patientSignup":
            document.getElementById("signupBtn").addEventListener("click", window.patientSignupHandler || signupPatient);
            break;
        case "patientLogin":
            document.getElementById("loginBtn").addEventListener("click", window.patientLoginHandler || loginPatient);
            break;
        case 'addDoctor':
            document.getElementById('saveDoctorBtn').addEventListener('click', window.adminAddDoctor || addDoctor);
            break;
        case 'adminLogin':
            document.getElementById('adminLoginBtn').addEventListener('click', window.adminLoginHandler || adminLogin);
            break;
        case 'doctorLogin':
            document.getElementById('doctorLoginBtn').addEventListener('click', window.doctorLoginHandler || doctorLogin);
            break;
        case 'editDoctor':
            document.getElementById('updateDoctorBtn').addEventListener('click', () => updateDoctor(data?.id));
            break;
        case 'bookAppointment':
            document.getElementById('confirmBookingBtn').addEventListener('click', () => confirmBooking(data));
            break;
    }

    // Add form submission on enter key
    const form = document.querySelector('.modal-form');
    if (form) {
        form.addEventListener('submit', (e) => e.preventDefault());
    }
}

// Helper function to get tomorrow's date
function getTomorrowDate() {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
}

// Helper function to generate time options
function generateTimeOptions(availableTimes) {
    if (!availableTimes || !Array.isArray(availableTimes)) {
        return `
            <option value="09:00-10:00">9:00 AM - 10:00 AM</option>
            <option value="10:00-11:00">10:00 AM - 11:00 AM</option>
            <option value="11:00-12:00">11:00 AM - 12:00 PM</option>
            <option value="14:00-15:00">2:00 PM - 3:00 PM</option>
            <option value="15:00-16:00">3:00 PM - 4:00 PM</option>
        `;
    }
    
    return availableTimes.map(time => 
        `<option value="${time}">${time.replace('-', ' - ')}</option>`
    ).join('');
}

// Fallback handler functions (in case service functions aren't available)
function signupPatient() {
    const name = document.getElementById('signupName').value;
    const email = document.getElementById('signupEmail').value;
    const password = document.getElementById('signupPassword').value;
    const phone = document.getElementById('signupPhone').value;
    const address = document.getElementById('signupAddress').value;

    if (!name || !email || !password || !phone || !address) {
        alert('Please fill in all fields');
        return;
    }

    console.log('Patient signup:', { name, email, phone, address });
    alert('Signup functionality will be implemented with backend API');
}

function loginPatient() {
    const email = document.getElementById('patientEmail').value;
    const password = document.getElementById('patientPassword').value;

    if (!email || !password) {
        alert('Please enter both email and password');
        return;
    }

    console.log('Patient login:', { email, password });
    alert('Login functionality will be implemented with backend API');
}

function addDoctor() {
    const name = document.getElementById('doctorName').value;
    const specialization = document.getElementById('specialization').value;
    const email = document.getElementById('doctorEmail').value;
    const password = document.getElementById('doctorPassword').value;
    const phone = document.getElementById('doctorPhone').value;
    
    // Get selected availability
    const availabilityCheckboxes = document.querySelectorAll('input[name="availability"]:checked');
    const availableTimes = Array.from(availabilityCheckboxes).map(cb => cb.value);

    if (!name || !specialization || !email || !password || !phone) {
        alert('Please fill in all required fields');
        return;
    }

    if (availableTimes.length === 0) {
        alert('Please select at least one availability time slot');
        return;
    }

    const doctorData = {
        name,
        specialty: specialization,
        email,
        password,
        phone,
        availableTimes
    };

    console.log('Add doctor:', doctorData);
    alert('Add doctor functionality will be implemented with backend API');
}

function adminLogin() {
    const username = document.getElementById('adminUsername').value;
    const password = document.getElementById('adminPassword').value;

    if (!username || !password) {
        alert('Please enter both username and password');
        return;
    }

    console.log('Admin login:', { username, password });
    alert('Admin login functionality will be implemented with backend API');
}

function doctorLogin() {
    const email = document.getElementById('doctorEmail').value;
    const password = document.getElementById('doctorPassword').value;

    if (!email || !password) {
        alert('Please enter both email and password');
        return;
    }

    console.log('Doctor login:', { email, password });
    alert('Doctor login functionality will be implemented with backend API');
}

function updateDoctor(doctorId) {
    const name = document.getElementById('editDoctorName').value;
    const specialization = document.getElementById('editSpecialization').value;
    const email = document.getElementById('editDoctorEmail').value;
    const phone = document.getElementById('editDoctorPhone').value;

    if (!name || !specialization || !email || !phone) {
        alert('Please fill in all fields');
        return;
    }

    const doctorData = {
        id: doctorId,
        name,
        specialty: specialization,
        email,
        phone
    };

    console.log('Update doctor:', doctorData);
    alert('Update doctor functionality will be implemented with backend API');
}

function confirmBooking(doctorData) {
    const date = document.getElementById('appointmentDate').value;
    const time = document.getElementById('appointmentTime').value;
    const symptoms = document.getElementById('symptoms').value;
    const notes = document.getElementById('notes').value;

    if (!date || !time || !symptoms) {
        alert('Please fill in all required fields');
        return;
    }

    const bookingData = {
        doctorId: doctorData.id,
        doctorName: doctorData.name,
        date,
        time,
        symptoms,
        notes
    };

    console.log('Book appointment:', bookingData);
    alert('Booking functionality will be implemented with backend API');
}

// Global close modal function
window.closeModal = function() {
    document.getElementById('modal').style.display = 'none';
};

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { openModal, closeModal: window.closeModal };
}