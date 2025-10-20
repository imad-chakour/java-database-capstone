/**
 * Doctor Card Component for Clinic Management System
 * Reusable card component for displaying doctor information
 */

// Import services (these will be implemented in next lab)
// import { deleteDoctor } from './services/doctorServices.js';
// import { getPatientData } from './services/patientServices.js';

export function createDoctorCard(doctor) {
    // Create main card container
    const card = document.createElement("div");
    card.classList.add("doctor-card");
    card.setAttribute("data-doctor-id", doctor.id);

    // Get user role
    const role = localStorage.getItem("userRole");

    // Create doctor info section
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    // Doctor name
    const name = document.createElement("h3");
    name.textContent = doctor.name || "Dr. Unknown";
    name.classList.add("doctor-name");

    // Doctor specialty
    const specialization = document.createElement("p");
    specialization.textContent = `Specialty: ${doctor.specialty || "General Practice"}`;
    specialization.classList.add("doctor-specialty");

    // Doctor email
    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email || "N/A"}`;
    email.classList.add("doctor-email");

    // Doctor phone
    const phone = document.createElement("p");
    phone.textContent = `Phone: ${doctor.phone || "N/A"}`;
    phone.classList.add("doctor-phone");

    // Doctor availability
    const availability = document.createElement("p");
    const availableTimes = doctor.availableTimes || doctor.availability || ["Not available"];
    availability.textContent = `Available: ${Array.isArray(availableTimes) ? availableTimes.join(", ") : availableTimes}`;
    availability.classList.add("doctor-availability");

    // Append all info elements
    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(phone);
    infoDiv.appendChild(availability);

    // Create actions container
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // Conditionally add buttons based on user role
    if (role === "admin") {
        // Delete button for admin
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.classList.add("delete-btn");
        removeBtn.addEventListener("click", async () => {
            const confirmDelete = confirm(`Are you sure you want to delete ${doctor.name}?`);
            if (confirmDelete) {
                try {
                    const token = localStorage.getItem("token");
                    // await deleteDoctor(doctor.id, token);
                    // For now, simulate deletion
                    console.log(`Deleting doctor: ${doctor.id}`);
                    card.remove();
                    showNotification(`Doctor ${doctor.name} deleted successfully`, "success");
                } catch (error) {
                    console.error("Error deleting doctor:", error);
                    showNotification("Failed to delete doctor", "error");
                }
            }
        });
        actionsDiv.appendChild(removeBtn);

        // Edit button for admin
        const editBtn = document.createElement("button");
        editBtn.textContent = "Edit";
        editBtn.classList.add("edit-btn");
        editBtn.addEventListener("click", () => {
            openEditDoctorModal(doctor);
        });
        actionsDiv.appendChild(editBtn);

    } else if (role === "patient") {
        // Book button for non-logged-in patient
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("book-btn");
        bookNow.addEventListener("click", () => {
            alert("Please log in to book an appointment.");
            openModal('patientLogin');
        });
        actionsDiv.appendChild(bookNow);

    } else if (role === "loggedPatient") {
        // Book button for logged-in patient
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("book-btn");
        bookNow.addEventListener("click", async (e) => {
            try {
                const token = localStorage.getItem("token");
                // const patientData = await getPatientData(token);
                // For now, use mock patient data
                const patientData = {
                    id: localStorage.getItem("patientId") || "patient-123",
                    name: localStorage.getItem("patientName") || "Patient User"
                };
                showBookingOverlay(e, doctor, patientData);
            } catch (error) {
                console.error("Error getting patient data:", error);
                showNotification("Failed to load patient data", "error");
            }
        });
        actionsDiv.appendChild(bookNow);

        // View Details button
        const detailsBtn = document.createElement("button");
        detailsBtn.textContent = "View Details";
        detailsBtn.classList.add("details-btn");
        detailsBtn.addEventListener("click", () => {
            showDoctorDetails(doctor);
        });
        actionsDiv.appendChild(detailsBtn);
    } else {
        // Default view for other roles or no role
        const viewBtn = document.createElement("button");
        viewBtn.textContent = "View Profile";
        viewBtn.classList.add("view-btn");
        viewBtn.addEventListener("click", () => {
            showDoctorDetails(doctor);
        });
        actionsDiv.appendChild(viewBtn);
    }

    // Append sections to main card
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}

// Helper function to show booking overlay
function showBookingOverlay(event, doctor, patientData) {
    console.log("Booking overlay for:", doctor.name);
    console.log("Patient data:", patientData);
    
    // Create ripple effect
    createRippleEffect(event);
    
    // Show booking modal
    showBookingModal(doctor, patientData);
}

// Helper function to create ripple effect
function createRippleEffect(event) {
    const button = event.currentTarget;
    const ripple = document.createElement("span");
    const rect = button.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = event.clientX - rect.left - size / 2;
    const y = event.clientY - rect.top - size / 2;
    
    ripple.style.width = ripple.style.height = size + 'px';
    ripple.style.left = x + 'px';
    ripple.style.top = y + 'px';
    ripple.classList.add('ripple');
    
    button.appendChild(ripple);
    
    setTimeout(() => {
        ripple.remove();
    }, 600);
}

// Helper function to show booking modal
function showBookingModal(doctor, patientData) {
    const modal = document.getElementById('modal');
    const modalBody = document.getElementById('modal-body');
    
    if (modal && modalBody) {
        modalBody.innerHTML = `
            <div class="booking-modal">
                <h3>Book Appointment with ${doctor.name}</h3>
                <p class="doctor-specialty-booking">${doctor.specialty}</p>
                
                <form id="bookingForm">
                    <div class="form-group">
                        <label for="appointmentDate">Appointment Date:</label>
                        <input type="date" id="appointmentDate" required min="${getTomorrowDate()}">
                    </div>
                    
                    <div class="form-group">
                        <label for="appointmentTime">Preferred Time:</label>
                        <select id="appointmentTime" required>
                            <option value="">Select Time</option>
                            ${generateTimeSlots(doctor.availableTimes)}
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="symptoms">Symptoms/Reason:</label>
                        <textarea id="symptoms" placeholder="Describe your symptoms or reason for visit..." required></textarea>
                    </div>
                    
                    <div class="form-group">
                        <label for="notes">Additional Notes:</label>
                        <textarea id="notes" placeholder="Any additional information..."></textarea>
                    </div>
                    
                    <div class="booking-actions">
                        <button type="button" class="cancel-btn" onclick="closeModal()">Cancel</button>
                        <button type="submit" class="confirm-booking-btn">Confirm Booking</button>
                    </div>
                </form>
            </div>
        `;
        
        modal.style.display = 'block';
        
        // Add form submission handler
        const form = document.getElementById('bookingForm');
        if (form) {
            form.addEventListener('submit', (e) => handleBookingSubmit(e, doctor, patientData));
        }
    }
}

// Helper function to get tomorrow's date
function getTomorrowDate() {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
}

// Helper function to generate time slots
function generateTimeSlots(availableTimes) {
    if (!availableTimes || !Array.isArray(availableTimes)) {
        return '<option value="09:00">09:00 AM</option><option value="10:00">10:00 AM</option>';
    }
    
    return availableTimes.map(time => {
        // Simple time formatting - you might want to enhance this
        const formattedTime = time.replace('-', ' - ');
        return `<option value="${time}">${formattedTime}</option>`;
    }).join('');
}

// Helper function to handle booking submission
function handleBookingSubmit(event, doctor, patientData) {
    event.preventDefault();
    
    const formData = {
        doctorId: doctor.id,
        patientId: patientData.id,
        appointmentDate: document.getElementById('appointmentDate').value,
        appointmentTime: document.getElementById('appointmentTime').value,
        symptoms: document.getElementById('symptoms').value,
        notes: document.getElementById('notes').value,
        status: 'scheduled'
    };
    
    console.log("Booking submitted:", formData);
    
    // Simulate API call
    setTimeout(() => {
        showNotification("Appointment booked successfully!", "success");
        closeModal();
    }, 1000);
}

// Helper function to show doctor details
function showDoctorDetails(doctor) {
    const modal = document.getElementById('modal');
    const modalBody = document.getElementById('modal-body');
    
    if (modal && modalBody) {
        modalBody.innerHTML = `
            <div class="doctor-details">
                <h3>${doctor.name}</h3>
                <p><strong>Specialty:</strong> ${doctor.specialty}</p>
                <p><strong>Email:</strong> ${doctor.email}</p>
                <p><strong>Phone:</strong> ${doctor.phone}</p>
                <p><strong>Availability:</strong> ${Array.isArray(doctor.availableTimes) ? doctor.availableTimes.join(', ') : doctor.availableTimes}</p>
                <p><strong>Experience:</strong> ${doctor.yearsOfExperience || 'Not specified'} years</p>
                <p><strong>License:</strong> ${doctor.licenseNumber || 'Not available'}</p>
                
                <div class="details-actions">
                    <button type="button" class="close-details-btn" onclick="closeModal()">Close</button>
                </div>
            </div>
        `;
        
        modal.style.display = 'block';
    }
}

// Helper function to open edit doctor modal (for admin)
function openEditDoctorModal(doctor) {
    const modal = document.getElementById('modal');
    const modalBody = document.getElementById('modal-body');
    
    if (modal && modalBody) {
        modalBody.innerHTML = `
            <div class="edit-doctor-modal">
                <h3>Edit Doctor: ${doctor.name}</h3>
                
                <form id="editDoctorForm">
                    <div class="form-group">
                        <label for="editDoctorName">Full Name:</label>
                        <input type="text" id="editDoctorName" value="${doctor.name}" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="editDoctorSpecialty">Specialty:</label>
                        <select id="editDoctorSpecialty" required>
                            <option value="Cardiologist" ${doctor.specialty === 'Cardiologist' ? 'selected' : ''}>Cardiologist</option>
                            <option value="Neurologist" ${doctor.specialty === 'Neurologist' ? 'selected' : ''}>Neurologist</option>
                            <option value="Orthopedist" ${doctor.specialty === 'Orthopedist' ? 'selected' : ''}>Orthopedist</option>
                            <option value="Pediatrician" ${doctor.specialty === 'Pediatrician' ? 'selected' : ''}>Pediatrician</option>
                            <option value="Dermatologist" ${doctor.specialty === 'Dermatologist' ? 'selected' : ''}>Dermatologist</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="editDoctorEmail">Email:</label>
                        <input type="email" id="editDoctorEmail" value="${doctor.email}" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="editDoctorPhone">Phone:</label>
                        <input type="tel" id="editDoctorPhone" value="${doctor.phone}" required>
                    </div>
                    
                    <div class="form-actions">
                        <button type="button" class="cancel-btn" onclick="closeModal()">Cancel</button>
                        <button type="submit" class="save-btn">Save Changes</button>
                    </div>
                </form>
            </div>
        `;
        
        modal.style.display = 'block';
        
        const form = document.getElementById('editDoctorForm');
        if (form) {
            form.addEventListener('submit', (e) => handleEditDoctor(e, doctor.id));
        }
    }
}

// Helper function to handle edit doctor form submission
function handleEditDoctor(event, doctorId) {
    event.preventDefault();
    
    const formData = {
        name: document.getElementById('editDoctorName').value,
        specialty: document.getElementById('editDoctorSpecialty').value,
        email: document.getElementById('editDoctorEmail').value,
        phone: document.getElementById('editDoctorPhone').value
    };
    
    console.log("Updating doctor:", doctorId, formData);
    
    // Simulate API call
    setTimeout(() => {
        showNotification("Doctor information updated successfully!", "success");
        closeModal();
        // Refresh the page or update the specific card
        window.location.reload();
    }, 1000);
}

// Helper function to show notifications
function showNotification(message, type = "info") {
    // Create notification element
    const notification = document.createElement("div");
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    // Add styles
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 5px;
        color: white;
        font-weight: bold;
        z-index: 10000;
        transition: all 0.3s ease;
        ${type === 'success' ? 'background: #4CAF50;' : ''}
        ${type === 'error' ? 'background: #f44336;' : ''}
        ${type === 'info' ? 'background: #2196F3;' : ''}
    `;
    
    document.body.appendChild(notification);
    
    // Remove after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Global function to close modal
function closeModal() {
    const modal = document.getElementById('modal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Make closeModal available globally
window.closeModal = closeModal;

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { createDoctorCard };
}