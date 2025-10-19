/**
 * Footer Component for Clinic Management System
 * Static footer that appears on all pages
 */

function renderFooter() {
    const footer = document.getElementById("footer");
    if (!footer) return;

    footer.innerHTML = `
        <footer class="footer">
            <div class="footer-content">
                <!-- Branding Section -->
                <div class="footer-logo">
                    <img src="../assets/images/logo/logo.png" alt="Hospital CMS Logo" class="footer-logo-img">
                    <p class="footer-copyright">© 2024 Hospital CMS. All rights reserved.</p>
                    <p class="footer-tagline">Providing quality healthcare management solutions</p>
                </div>
                
                <!-- Links Sections -->
                <div class="footer-links">
                    <!-- Company Column -->
                    <div class="footer-column">
                        <h4 class="footer-heading">Company</h4>
                        <ul class="footer-list">
                            <li><a href="/about" class="footer-link">About Us</a></li>
                            <li><a href="/careers" class="footer-link">Careers</a></li>
                            <li><a href="/press" class="footer-link">Press</a></li>
                            <li><a href="/blog" class="footer-link">Blog</a></li>
                        </ul>
                    </div>
                    
                    <!-- Support Column -->
                    <div class="footer-column">
                        <h4 class="footer-heading">Support</h4>
                        <ul class="footer-list">
                            <li><a href="/account" class="footer-link">Account</a></li>
                            <li><a href="/help" class="footer-link">Help Center</a></li>
                            <li><a href="/contact" class="footer-link">Contact Us</a></li>
                            <li><a href="/faq" class="footer-link">FAQ</a></li>
                        </ul>
                    </div>
                    
                    <!-- Legal Column -->
                    <div class="footer-column">
                        <h4 class="footer-heading">Legal</h4>
                        <ul class="footer-list">
                            <li><a href="/terms" class="footer-link">Terms of Service</a></li>
                            <li><a href="/privacy" class="footer-link">Privacy Policy</a></li>
                            <li><a href="/licensing" class="footer-link">Licensing</a></li>
                            <li><a href="/compliance" class="footer-link">Compliance</a></li>
                        </ul>
                    </div>
                    
                    <!-- Contact Column -->
                    <div class="footer-column">
                        <h4 class="footer-heading">Contact</h4>
                        <ul class="footer-list">
                            <li class="footer-contact">
                                <i class="contact-icon">📍</i>
                                123 Healthcare St, Medical City
                            </li>
                            <li class="footer-contact">
                                <i class="contact-icon">📞</i>
                                +1 (555) 123-4567
                            </li>
                            <li class="footer-contact">
                                <i class="contact-icon">✉️</i>
                                info@hospitalcms.com
                            </li>
                            <li class="footer-contact">
                                <i class="contact-icon">🕒</i>
                                Mon-Fri: 8AM-6PM
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            
            <!-- Bottom Bar -->
            <div class="footer-bottom">
                <p class="footer-bottom-text">
                    Built with ❤️ for better healthcare management
                </p>
            </div>
        </footer>
    `;
}

// Call the function to render footer when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    renderFooter();
});

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { renderFooter };
}