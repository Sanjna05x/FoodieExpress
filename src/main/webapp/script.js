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

// Place Order from cart.html
function loadCartPage() {
    cartItems = JSON.parse(localStorage.getItem('foodieCart')) || [];
    if (cartItems.length === 0) {
        document.getElementById('cartItemsContainer').innerHTML = '<div class="alert alert-info"><i class="fas fa-info-circle me-2"></i>Your cart is empty. <a href="menu.html" class="text-orange fw-bold">Continue Shopping →</a></div>';
        document.getElementById('placeOrderBtn').disabled = true;
        document.getElementById('cartItemCount').textContent = '0 items';
        return;
    }
    
    document.getElementById('cartItemCount').textContent = cartItems.length + ' items';
    renderCartPageItems();
    calcOrderTotals();
    document.getElementById('placeOrderBtn').disabled = false;
}

function renderCartPageItems() {
    const container = document.getElementById('cartItemsContainer');
    let html = '';
    
    cartItems.forEach((item, index) => {
        const itemTotal = item.price * item.quantity;
        html += `
        <div class="card mb-3 shadow-sm border-0">
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-2">
                        <div class="bg-light rounded-circle d-flex align-items-center justify-content-center mx-auto" style="width: 60px; height: 60px;">
                            <i class="fas fa-utensils text-orange fs-4"></i>
                        </div>
                    </div>
                    <div class="col-5">
                        <h6 class="fw-bold mb-1">${item.name}</h6>
                        <small class="text-muted">₹${item.price}</small>
                    </div>
                    <div class="col-3">
                        <div class="input-group input-group-sm">
                            <button class="btn btn-outline-secondary" onclick="updateQuantity(${item.id}, -1)">-</button>
                            <input type="number" class="form-control text-center" value="${item.quantity}" readonly>
                            <button class="btn btn-outline-secondary" onclick="updateQuantity(${item.id}, 1)">+</button>
                        </div>
                    </div>
                    <div class="col-2 text-end">
                        <h5 class="fw-bold text-orange mb-0">₹${itemTotal.toFixed(2)}</h5>
                        <button class="btn btn-sm btn-outline-danger mt-1" onclick="removeFromCart(${item.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>`;
    });
    
    container.innerHTML = html;
}

function calcOrderTotals() {
    let subtotal = 0;
    cartItems.forEach(item => subtotal += item.price * item.quantity);
    
    const tax = subtotal * 0.05;
    const grandTotal = subtotal + tax;
    
    document.getElementById('subtotal').textContent = `₹${subtotal.toFixed(2)}`;
    document.getElementById('tax').textContent = `₹${tax.toFixed(2)}`;
    document.getElementById('grandTotal').textContent = `₹${grandTotal.toFixed(2)}`;
    document.getElementById('orderTotal').textContent = grandTotal.toFixed(2);
}

function updateQuantity(id, delta) {
    const item = cartItems.find(i => i.id === id);
    if (item) {
        item.quantity += delta;
        if (item.quantity <= 0) {
            removeFromCart(id);
        } else {
            localStorage.setItem('foodieCart', JSON.stringify(cartItems));
            loadCartPage();
        }
    }
}

async function placeOrder() {
    const address = document.getElementById('deliveryAddress').value;

    if (!address.trim()) {
        alert('Please enter delivery address!');
        return;
    }
    
    const cartData = cartItems.map((item, index) => ({
        name: `item_${index}`,
        value: JSON.stringify({id: item.id, price: item.price, quantity: item.quantity})
    }));
    
    const formData = new FormData();
    cartData.forEach(({name, value}) => formData.append(name, value));
    formData.append('address', address);
    
    try {
        const response = await fetch('/FoodieExpress/order', {
            method: 'POST',
            body: formData
        });
        
        if (response.ok) {
            localStorage.removeItem('foodieCart');
            cartItems = [];
            alert('Order placed successfully!');
            window.location.href = 'order-status.html';
        } else {
            alert('Order failed!');
        }
    } catch (error) {
        alert('Error placing order!');
    }
}

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

