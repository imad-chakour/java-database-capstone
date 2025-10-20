/**
 * Patient Services Module
 * Handles all API interactions related to patient data
 * Includes signup, login, appointment management, and data retrieval
 */

import { API_BASE_URL } from "../config/config.js";

// Patient API endpoint
const PATIENT_API = API_BASE_URL + '/patient';

/**
 * Patient signup - creates a new patient in the database
 * @param {Object} data - Patient data (name, email, password, phone, address)
 * @returns {Promise<Object>} Response object with success status and message
 */
export async function patientSignup(data) {
    try {
        const response = await fetch(`${PATIENT_API}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        const result = await response.json();
        
        if (response.ok) {
            return {
                success: true,
                message: result.message || 'Patient registered successfully',
                patientId: result.patientId || null
            };
        } else {
            return {
                success: false,
                message: result.message || 'Registration failed'
            };
        }
    } catch (error) {
        console.error("Error in patientSignup:", error);
        return {
            success: false,
            message: 'Network error: Failed to register patient'
        };
    }
}

/**
 * Patient login - authenticates patient credentials
 * @param {Object} data - Login credentials (email, password)
 * @returns {Promise<Object>} Response object with token and patient data
 */
export async function patientLogin(data) {
    try {
        const response = await fetch(`${PATIENT_API}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        const result = await response.json();
        
        if (response.ok) {
            return {
                success: true,
                token: result.token,
                patient: result.patient,
                message: result.message || 'Login successful'
            };
        } else {
            return {
                success: false,
                message: result.message || 'Login failed'
            };
        }
    } catch (error) {
        console.error("Error in patientLogin:", error);
        return {
            success: false,
            message: 'Network error: Failed to login'
        };
    }
}

/**
 * Get patient data (name, id, etc.) - used in booking appointments
 * @param {string} token - Authentication token
 * @returns {Promise<Object>} Patient object or null if error
 */
export async function getPatientData(token) {
    try {
        const response = await fetch(`${PATIENT_API}/data`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            return data.patient || data;
        } else {
            console.error("Failed to fetch patient data:", response.status);
            return null;
        }
    } catch (error) {
        console.error("Error fetching patient details:", error);
        return null;
    }
}

/**
 * Get patient by ID
 * @param {string} patientId - Patient ID
 * @param {string} token - Authentication token
 * @returns {Promise<Object>} Patient object or null if error
 */
export async function getPatientById(patientId, token) {
    try {
        const response = await fetch(`${PATIENT_API}/${patientId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            return data.patient || data;
        } else {
            console.error("Failed to fetch patient by ID:", response.status);
            return null;
        }
    } catch (error) {
        console.error("Error fetching patient by ID:", error);
        return null;
    }
}

/**
 * Get patient appointments - works for both doctor and patient dashboards
 * The backend API for fetching patient records (visible in Doctor Dashboard) 
 * and appointments (visible in Patient Dashboard) are the same, based on user role
 * @param {string} id - Patient ID or doctor ID based on context
 * @param {string} token - Authentication token
 * @param {string} user - User role ('patient' or 'doctor')
 * @returns {Promise<Array>} Array of appointments or null if error
 */
export async function getPatientAppointments(id, token, user = 'patient') {
    try {
        const response = await fetch(`${PATIENT_API}/appointments/${id}?user=${user}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            return data.appointments || [];
        } else {
            console.error("Failed to fetch patient appointments:", response.status);
            return [];
        }
    } catch (error) {
        console.error("Error fetching patient appointments:", error);
        return [];
    }
}

/**
 * Filter appointments based on condition and name
 * @param {string} condition - Filter condition (e.g., 'pending', 'completed', 'cancelled')
 * @param {string} name - Patient name or doctor name to filter by
 * @param {string} token - Authentication token
 * @returns {Promise<Array>} Filtered array of appointments
 */
export async function filterAppointments(condition, name, token) {
    try {
        const params = new URLSearchParams();
        if (condition) params.append('condition', condition);
        if (name) params.append('name', name);

        const response = await fetch(`${PATIENT_API}/appointments/filter?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            return data.appointments || [];
        } else {
            console.error("Failed to filter appointments:", response.status);
            return [];
        }
    } catch (error) {
        console.error("Error filtering appointments:", error);
        alert("Something went wrong while filtering appointments!");
        return [];
    }
}

/**
 * Book a new appointment
 * @param {Object} appointmentData - Appointment details
 * @param {string} token - Authentication token
 * @returns {Promise<Object>} Response object with success status and message
 */
export async function bookAppointment(appointmentData, token) {
    try {
        const response = await fetch(`${PATIENT_API}/appointments`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(appointmentData)
        });

        const result = await response.json();
        
        if (response.ok) {
            return {
                success: true,
                message: result.message || 'Appointment booked successfully',
                appointment: result.appointment || null
            };
        } else {
            return {
                success: false,
                message: result.message || 'Failed to book appointment'
            };
        }
    } catch (error) {
        console.error("Error booking appointment:", error);
        return {
            success: false,
            message: 'Network error: Failed to book appointment'
        };
    }
}

/**
 * Update appointment status (cancel, reschedule, etc.)
 * @param {string} appointmentId - Appointment ID
 * @param {string} status - New status ('cancelled', 'rescheduled', etc.)
 * @param {string} token - Authentication token
 * @returns {Promise<Object>} Response object with success status and message
 */
export async function updateAppointmentStatus(appointmentId, status, token) {
    try {
        const response = await fetch(`${PATIENT_API}/appointments/${appointmentId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ status })
        });

        const result = await response.json();
        
        if (response.ok) {
            return {
                success: true,
                message: result.message || 'Appointment updated successfully'
            };
        } else {
            return {
                success: false,
                message: result.message || 'Failed to update appointment'
            };
        }
    } catch (error) {
        console.error("Error updating appointment:", error);
        return {
            success: false,
            message: 'Network error: Failed to update appointment'
        };
    }
}

/**
 * Get appointment by ID
 * @param {string} appointmentId - Appointment ID
 * @param {string} token - Authentication token
 * @returns {Promise<Object>} Appointment object or null if error
 */
export async function getAppointmentById(appointmentId, token) {
    try {
        const response = await fetch(`${PATIENT_API}/appointments/${appointmentId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            return data.appointment || data;
        } else {
            console.error("Failed to fetch appointment:", response.status);
            return null;
        }
    } catch (error) {
        console.error("Error fetching appointment:", error);
        return null;
    }
}

/**
 * Update patient profile
 * @param {string} patientId - Patient ID
 * @param {Object} updateData - Updated patient data
 * @param {string} token - Authentication token
 * @returns {Promise<Object>} Response object with success status and message
 */
export async function updatePatientProfile(patientId, updateData, token) {
    try {
        const response = await fetch(`${PATIENT_API}/${patientId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(updateData)
        });

        const result = await response.json();
        
        if (response.ok) {
            return {
                success: true,
                message: result.message || 'Profile updated successfully',
                patient: result.patient || null
            };
        } else {
            return {
                success: false,
                message: result.message || 'Failed to update profile'
            };
        }
    } catch (error) {
        console.error("Error updating patient profile:", error);
        return {
            success: false,
            message: 'Network error: Failed to update profile'
        };
    }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        patientSignup,
        patientLogin,
        getPatientData,
        getPatientById,
        getPatientAppointments,
        filterAppointments,
        bookAppointment,
        updateAppointmentStatus,
        getAppointmentById,
        updatePatientProfile
    };
}