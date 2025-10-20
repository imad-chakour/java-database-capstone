// render.js
// In your render.js or main JavaScript file
window.selectRole = function(role) {
    if (role === 'patient') {
        window.location.href = '/patient';
    } else if (role === 'admin') {
        // Check if admin is already logged in
        const token = localStorage.getItem('token');
        const userRole = localStorage.getItem('userRole');
        
        if (token && userRole === 'admin') {
            window.location.href = '/admin';
        } else {
            // Show admin login modal
            openModal('adminLogin');
        }
    } else if (role === 'doctor') {
        // Check if doctor is already logged in
        const token = localStorage.getItem('token');
        const userRole = localStorage.getItem('userRole');
        
        if (token && userRole === 'doctor') {
            window.location.href = '/doctor';
        } else {
            // Show doctor login modal
            openModal('doctorLogin');
        }
    }
};

// Update your admin login handler to redirect after successful login
window.adminLoginHandler = async function(event) {
    event.preventDefault();
    
    const username = document.getElementById('adminUsername').value;
    const password = document.getElementById('adminPassword').value;

    if (!username || !password) {
        alert('Please enter both username and password');
        return;
    }

    const loginData = {
        identifier: username,
        password: password
    };

    try {
        const response = await fetch('/api/admin/login', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });

        if (response.ok) {
            const data = await response.json();
            
            // Store token and role
            localStorage.setItem('token', data.token);
            localStorage.setItem('userRole', 'admin');
            
            // Close modal
            closeModal();
            
            // Redirect to admin dashboard
            window.location.href = '/admin';
            
        } else {
            const errorData = await response.json();
            alert(errorData.error || 'Invalid credentials!');
        }
    } catch (error) {
        console.error('Admin login error:', error);
        alert('Login failed. Please try again.');
    }
};

// Update your doctor login handler similarly
window.doctorLoginHandler = async function(event) {
    event.preventDefault();
    
    const email = document.getElementById('doctorEmail').value;
    const password = document.getElementById('doctorPassword').value;

    if (!email || !password) {
        alert('Please enter both email and password');
        return;
    }

    const loginData = {
        identifier: email,
        password: password
    };

    try {
        const response = await fetch('/api/doctor/login', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });

        if (response.ok) {
            const data = await response.json();
            
            // Store token and role
            localStorage.setItem('token', data.token);
            localStorage.setItem('userRole', 'doctor');
            localStorage.setItem('doctorId', data.doctorId || '');
            
            // Close modal
            closeModal();
            
            // Redirect to doctor dashboard
            window.location.href = '/doctor';
            
        } else {
            const errorData = await response.json();
            alert(errorData.error || 'Invalid credentials!');
        }
    } catch (error) {
        console.error('Doctor login error:', error);
        alert('Login failed. Please try again.');
    }
};


function renderContent() {
  const role = getRole();
  if (!role) {
    window.location.href = "/"; // if no role, send to role selection page
    return;
  }
}
