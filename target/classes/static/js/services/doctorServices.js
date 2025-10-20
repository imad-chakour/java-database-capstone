/**
 * Doctor Services Module
 * Handles all API interactions related to doctor data
 */

import { API_BASE_URL } from "../config/config.js";

// Doctor API endpoint
const DOCTOR_API = API_BASE_URL + '/doctor';

/**
 * Get all doctors from the API
 * @returns {Promise<Array>} Array of doctor objects
 */
export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data.doctors || data || [];
        
    } catch (error) {
        console.error('Error fetching doctors:', error);
        // Return empty array to prevent frontend breakage
        return [];
    }
}

/**
 * Delete a specific doctor
 * @param {string|number} id - Doctor ID to delete
 * @param {string} token - Authentication token
 * @returns {Promise<Object>} Response object with success status and message
 */
export async function deleteDoctor(id, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}?token=${token}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        const data = await response.json();
        
        if (response.ok) {
            return {
                success: true,
                message: data.message || 'Doctor deleted successfully'
            };
        } else {
            return {
                success: false,
                message: data.message || 'Failed to delete doctor'
            };
        }
        
    } catch (error) {
        console.error('Error deleting doctor:', error);
        return {
            success: false,
            message: 'Network error: Failed to delete doctor'
        };
    }
}

/**
 * Save (add) a new doctor
 * @param {Object} doctor - Doctor object with details
 * @param {string} token - Authentication token
 * @returns {Promise<Object>} Response object with success status and message
 */
export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(`${DOCTOR_API}?token=${token}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(doctor)
        });

        const data = await response.json();
        
        if (response.ok) {
            return {
                success: true,
                message: data.message || 'Doctor saved successfully',
                doctor: data.doctor || null
            };
        } else {
            return {
                success: false,
                message: data.message || 'Failed to save doctor'
            };
        }
        
    } catch (error) {
        console.error('Error saving doctor:', error);
        return {
            success: false,
            message: 'Network error: Failed to save doctor'
        };
    }
}

/**
 * Update an existing doctor
 * @param {string|number} id - Doctor ID to update
 * @param {Object} doctor - Updated doctor data
 * @param {string} token - Authentication token
 * @returns {Promise<Object>} Response object with success status and message
 */
export async function updateDoctor(id, doctor, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}?token=${token}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(doctor)
        });

        const data = await response.json();
        
        if (response.ok) {
            return {
                success: true,
                message: data.message || 'Doctor updated successfully',
                doctor: data.doctor || null
            };
        } else {
            return {
                success: false,
                message: data.message || 'Failed to update doctor'
            };
        }
        
    } catch (error) {
        console.error('Error updating doctor:', error);
        return {
            success: false,
            message: 'Network error: Failed to update doctor'
        };
    }
}

/**
 * Filter doctors based on criteria
 * @param {string} name - Doctor name to filter by
 * @param {string} time - Time availability to filter by
 * @param {string} specialty - Specialty to filter by
 * @returns {Promise<Array>} Filtered array of doctor objects
 */
export async function filterDoctors(name = '', time = '', specialty = '') {
    try {
        // Build query parameters
        const params = new URLSearchParams();
        if (name) params.append('name', name);
        if (time) params.append('time', time);
        if (specialty) params.append('specialty', specialty);

        const url = `${DOCTOR_API}/filter?${params.toString()}`;
        
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data.doctors || data || [];
        
    } catch (error) {
        console.error('Error filtering doctors:', error);
        alert('Failed to filter doctors. Please try again.');
        return [];
    }
}

/**
 * Get doctor by ID
 * @param {string|number} id - Doctor ID
 * @returns {Promise<Object>} Doctor object
 */
export async function getDoctorById(id) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data.doctor || data || null;
        
    } catch (error) {
        console.error('Error fetching doctor:', error);
        return null;
    }
}

/**
 * Get doctors by specialty
 * @param {string} specialty - Medical specialty
 * @returns {Promise<Array>} Array of doctors in the specified specialty
 */
export async function getDoctorsBySpecialty(specialty) {
    try {
        const response = await fetch(`${DOCTOR_API}/specialty/${specialty}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data.doctors || data || [];
        
    } catch (error) {
        console.error('Error fetching doctors by specialty:', error);
        return [];
    }
}

/**
 * Get available time slots for a doctor
 * @param {string|number} doctorId - Doctor ID
 * @returns {Promise<Array>} Array of available time slots
 */
export async function getDoctorAvailability(doctorId) {
    try {
        const response = await fetch(`${DOCTOR_API}/${doctorId}/availability`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data.availability || data || [];
        
    } catch (error) {
        console.error('Error fetching doctor availability:', error);
        return [];
    }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        getDoctors,
        deleteDoctor,
        saveDoctor,
        updateDoctor,
        filterDoctors,
        getDoctorById,
        getDoctorsBySpecialty,
        getDoctorAvailability
    };
}