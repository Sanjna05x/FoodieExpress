// script.js - Complete FoodieExpress Menu + Cart System for Beginners
// Features: AJAX fetch, search/filter, cart sidebar, localStorage, animations
// Must run from http://localhost:9090/FoodieExpress/menu.html (not file://)

// Global variables
let allMenuItems = [];
let cartItems = JSON.parse(localStorage.getItem('foodieCart')) || [];
let currentCategory = 'All';
let searchTerm = '';

// Update cart badge
function updateCartBadge() {
    const badge = document.getElementById('cartCountBadge');
    const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0);
    badge.textContent = totalItems;
    badge.style.display = totalItems > 0 ? 'inline-block' : 'none';
}

// Load menu items from MenuServlet
async function loadMenuItems() {
    try {
        document.getElementById('loadingSpinner').classList.remove('d-none');
        document.getElementById('foodGrid').innerHTML = '';
        document.getElementById('noResults').classList.add('d-none');
        
        // FIXED: Correct localhost path for servlet
        let url = `http://localhost:${window.location.port || 9090}/FoodieExpress/MenuServlet`;
        if (currentCategory !== 'All') url += `?category=${encodeURIComponent(currentCategory)}`;
        if (searchTerm) url += `${url.includes('?') ? '&' : '?'}search=${encodeURIComponent(searchTerm)}`;
        
        const response = await fetch(url);
        const items = await response.json();
        
        allMenuItems = items;
        renderFoodCards(items);
        
    } catch (error) {
        console.error('Fetch error:', error);
        document.getElementById('foodGrid').innerHTML = `
            <div class="col-12">
                <div class="alert alert-danger">
                    <h5>❌ Failed to load menu</h5>
                    <p>Make sure server is running: <code>mvn cargo:run</code></p>
                    <button class="btn btn-orange" onclick="loadMenuItems()">Retry</button>
                </div>
            </div>
        `;
    } finally {
        document.getElementById('loadingSpinner').classList.add('d-none');
    }
}

// Render food cards grid
function renderFoodCards(items) {
    const grid = document.getElementById('foodGrid');
    
    if (items.length === 0) {
        grid.innerHTML = '<div class="col-12"><div class="text-center py-5"><i class="fas fa-inbox fa-3x text-muted mb-3"></i><h4 class="text-muted">No items found</h4></div></div>';
        return;
    }
    
    let html = '';
    items.forEach(item => {
        html += `
        <div class="col-lg-3 col-md-4 col-sm-6">
            <div class="card food-card h-100 position-relative overflow-hidden">
                <!-- Food Image -->
                <div class="position-relative overflow-hidden" style="height: 200px;">

                    <i class="fas fa-utensils fa-3x text-muted position-absolute top-50 start-50 translate-middle"></i>

                </div>
                
                <!-- Category Badge -->
                <span class="position-absolute top-1 start-2 badge bg-orange">${item.category}</span>
                
                <!-- Content -->
                <div class="card-body p-4 pt-2">
                    <h5 class="card-title fw-bold lh-sm mb-2">${item.name}</h5>
                    <p class="card-text text-muted small mb-3">${item.description}</p>
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="h4 fw-bold text-orange mb-0">₹${item.price}</span>
                    </div>
                    <button class="btn btn-orange w-100 py-2 fw-semibold" onclick="addToCart(${item.id}, '${item.name}', ${item.price})">
                        <i class="fas fa-shopping-cart me-2"></i>Add to Cart
                    </button>
                </div>
            </div>
        </div>`;
    });
    
    grid.innerHTML = html;
}

// Add to cart + open sidebar
function addToCart(id, name, price) {
    // Find or create cart item
    let cartItem = cartItems.find(item => item.id === id);
    if (cartItem) {
        cartItem.quantity += 1;
    } else {
        cartItem = { id, name, price, quantity: 1 };
        cartItems.push(cartItem);
    }
    
    // Save to localStorage
    localStorage.setItem('foodieCart', JSON.stringify(cartItems));
    updateCartBadge();
    
    // Open cart sidebar
    openCartSidebar();
    
    // Success feedback
    showToast('Added to cart! 🛒', 'success');
}

// Open cart sidebar
function openCartSidebar() {
    document.getElementById('cartSidebar').classList.add('show');
    document.getElementById('cartOverlay').classList.remove('d-none');
    renderCartItems();
}

// Close cart sidebar
function closeCart() {
    document.getElementById('cartSidebar').classList.remove('show');
    document.getElementById('cartOverlay').classList.add('d-none');
}

// Render cart items in sidebar
function renderCartItems() {
    const container = document.getElementById('cartItemsList');
    let html = '';
    
    let total = 0;
    cartItems.forEach(item => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;
        
        html += `
        <div class="cart-item d-flex align-items-center p-3 border-bottom">
            <div class="flex-shrink-0 me-3">
                <div class="bg-light rounded-circle d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                    <i class="fas fa-utensils text-orange fs-5"></i>
                </div>
            </div>
            <div class="flex-grow-1">
                <h6 class="mb-1 fw-bold">${item.name}</h6>
                <small class="text-muted">₹${item.price} × ${item.quantity}</small>
            </div>
            <div class="text-end">
                <div class="fw-bold text-orange fs-5 mb-1">₹${itemTotal.toFixed(2)}</div>
                <button class="btn btn-sm btn-outline-danger" onclick="removeFromCart(${item.id})">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        </div>`;
    });
    
    container.innerHTML = html || '<div class="text-center py-4 text-muted">Your cart is empty</div>';
    document.getElementById('cartTotal').textContent = `₹${total.toFixed(2)}`;
    document.getElementById('finalTotal').textContent = total.toFixed(2);
}

// Remove item from cart
function removeFromCart(id) {
    cartItems = cartItems.filter(item => item.id !== id);
    localStorage.setItem('foodieCart', JSON.stringify(cartItems));
    updateCartBadge();
    renderCartItems();
    showToast('Removed from cart', 'warning');
}

// Checkout - redirect to cart or order page
function checkout() {
    closeCart();
    window.location.href = 'cart.html';
}

// Category filter
function filterByCategory(category) {
    currentCategory = category;
    
    // Update button styles
    document.querySelectorAll('#categoryFilters .btn').forEach(btn => {
        btn.classList.remove('btn-orange', 'active');
        btn.classList.add('btn-outline-orange');
    });
    
    event.target.classList.add('btn-orange', 'active');
    event.target.classList.remove('btn-outline-orange');
    
    loadMenuItems();
}

// Live search
document.getElementById('searchInput').addEventListener('input', function() {
    searchTerm = this.value;
    loadMenuItems();
});

// Load menu on page load
window.addEventListener('load', () => {
    updateCartBadge();
    loadMenuItems();
});

// Toast notification
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast align-items-center border-0 position-fixed ${type === 'success' ? 'bg-success text-white' : 'bg-warning text-dark'}`;
    toast.style.cssText = 'top: 20px; right: 20px; z-index: 10000; min-width: 300px;';
    
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;
    
    document.body.appendChild(toast);
    
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
    
    toast.addEventListener('hidden.bs.toast', () => toast.remove());
}

