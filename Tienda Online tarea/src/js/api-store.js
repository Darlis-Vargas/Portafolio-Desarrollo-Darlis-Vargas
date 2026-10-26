// 1. Obtener los productos de la API
async function obtenerProductos() {
    try {
        const response = await fetch('https://fakestoreapi.com/products');
        if (!response.ok) {
            throw new Error('Error al obtener los productos');
        }
        return await response.json();
    } catch (error) {
        console.error('Error:', error);
        return [];
    }
}

// 2. Renderizar todos los productos en el catálogo
function mostrarCatalogo(productos) {
    const contenedor = document.getElementById('catalogo');
    contenedor.innerHTML = productos.map(producto => `
        <div class="col-12 col-sm-6 col-md-4 col-lg-3 mb-4">
            <div class="card h-100 shadow-sm">
                <div class="p-4 d-flex justify-content-center align-items-center img-container">
                    <img src="${producto.image}" class="card-img-top" alt="${producto.title}">
                </div>
                <div class="card-body d-flex flex-column border-top">
                    <h6 class="card-title text-truncate" title="${producto.title}">${producto.title}</h6>
                    <p class="card-text fw-bold fs-5 mt-auto">$${producto.price}</p>
                    <button class="btn btn-primary w-100 mt-2" onclick="mostrarDetalles(${producto.id})">Ver Detalles</button>
                </div>
            </div>
        </div>
    `).join('');
}

// 3. Renderizar las 3 ofertas específicas (índices 0, 3 y 6)
function mostrarOfertas(productos) {
    const contenedor = document.getElementById('ofertas');
    // Filtrar los productos en las posiciones 1º, 4º y 7º
    const ofertas = [productos[0], productos[3], productos[6]].filter(Boolean);
    
    contenedor.innerHTML = ofertas.map(producto => `
        <div class="col-12 col-md-4 mb-4">
            <div class="card h-100 shadow-sm border-danger">
                <div class="position-absolute top-0 end-0 p-2">
                    <span class="badge bg-danger px-3 py-2">¡OFERTA!</span>
                </div>
                <div class="p-4 d-flex justify-content-center align-items-center img-container">
                    <img src="${producto.image}" class="card-img-top" alt="${producto.title}">
                </div>
                <div class="card-body d-flex flex-column border-top">
                    <h6 class="card-title text-truncate" title="${producto.title}">${producto.title}</h6>
                    <p class="card-text fw-bold fs-4 text-danger mt-auto">$${producto.price}</p>
                    <button class="btn btn-outline-danger w-100 mt-2" onclick="mostrarDetalles(${producto.id})">Ver Oferta</button>
                </div>
            </div>
        </div>
    `).join('');
}

// 4. Crear carrusel con 3 productos aleatorios
function mostrarCarrusel(productos) {
    const contenedor = document.getElementById('carrusel');
    const productosAleatorios = [...productos].sort(() => 0.5 - Math.random()).slice(0, 3);

    const indicadoresHTML = productosAleatorios.map((_, index) => `
        <button type="button" data-bs-target="#carrusel" data-bs-slide-to="${index}" ${index === 0 ? 'class="active" aria-current="true"' : ''} aria-label="Slide ${index + 1}"></button>
    `).join('');

    const itemsHTML = productosAleatorios.map((producto, index) => `
        <div class="carousel-item ${index === 0 ? 'active' : ''} bg-white p-5 text-center" style="height: 400px;">
            <img src="${producto.image}" class="d-inline-block h-100" style="object-fit: contain; max-width: 100%;" alt="${producto.title}">
            <div class="carousel-caption d-none d-md-block bg-dark bg-opacity-75 rounded px-3 py-2 bottom-0 mb-4">
                <h5 class="text-truncate">${producto.title}</h5>
                <button class="btn btn-light btn-sm mt-2" onclick="mostrarDetalles(${producto.id})">Ver más detalles</button>
            </div>
        </div>
    `).join('');

    contenedor.innerHTML = `
        <div class="carousel-indicators">${indicadoresHTML}</div>
        <div class="carousel-inner">${itemsHTML}</div>
        <button class="carousel-control-prev" type="button" data-bs-target="#carrusel" data-bs-slide="prev">
            <span class="carousel-control-prev-icon rounded-circle bg-dark p-3" aria-hidden="true"></span>
            <span class="visually-hidden">Anterior</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#carrusel" data-bs-slide="next">
            <span class="carousel-control-next-icon rounded-circle bg-dark p-3" aria-hidden="true"></span>
            <span class="visually-hidden">Siguiente</span>
        </button>
    `;
}

// 5. Mostrar modal con detalles de un producto mediante un nuevo Fetch
async function mostrarDetalles(idProducto) {
    try {
        const response = await fetch(`https://fakestoreapi.com/products/${idProducto}`);
        if (!response.ok) throw new Error('Error al obtener detalles del producto');
        const producto = await response.json();

        const modalContent = document.querySelector('#productoModal .modal-content');
        modalContent.innerHTML = `
            <div class="modal-header">
                <h5 class="modal-title fw-bold text-truncate" style="max-width: 90%;">${producto.title}</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body p-4">
                <div class="row align-items-center">
                    <div class="col-md-5 text-center mb-4 mb-md-0">
                        <img src="${producto.image}" alt="${producto.title}" class="img-fluid" style="max-height: 250px; object-fit: contain;">
                    </div>
                    <div class="col-md-7">
                        <span class="badge bg-secondary mb-2 text-uppercase">${producto.category}</span>
                        <h3 class="fw-bold mb-3">$${producto.price}</h3>
                        <p class="text-muted mb-4">${producto.description}</p>
                        <div class="d-flex align-items-center">
                            <span class="me-2 fw-bold">Rating:</span>
                            <span class="text-warning me-2">★ ${producto.rating.rate}</span>
                            <small class="text-muted">(${producto.rating.count} reseñas)</small>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
            </div>
        `;

        const modal = new bootstrap.Modal(document.getElementById('productoModal'));
        modal.show();
    } catch (error) {
        console.error('Error:', error);
        alert('No se pudieron cargar los detalles del producto en este momento.');
    }
}

// 6. Código de inicialización
async function inicializar() {
    const productos = await obtenerProductos();
    if (productos.length > 0) {
        mostrarCarrusel(productos);
        mostrarOfertas(productos);
        mostrarCatalogo(productos);
    }
}

// Ejecutar cuando el DOM esté completamente cargado
document.addEventListener('DOMContentLoaded', inicializar);