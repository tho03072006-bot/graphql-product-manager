'use strict';
const $ = (selector) => document.querySelector(selector);
const productFields = 'id title quantity desc price userid category { id name images } user { id fullname }';
let state = { products: [], categories: [], users: [] };
let filteredProducts = [];
let pendingDelete = null;
let filterRequest = 0;
const money = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 2 });

// AJAX: gửi query/mutation và variables đến cùng một endpoint, không tải lại trang.
async function graphql(query, variables = {}) {
  const response = await fetch('graphql', {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query, variables })
  });
  if (!response.ok) throw new Error(`Không kết nối được API (HTTP ${response.status}).`);
  const result = await response.json();
  // GraphQL có thể trả HTTP 200 nhưng vẫn chứa errors.
  if (result.errors?.length) throw new Error(result.errors.map(error => error.message).join('\n'));
  return result.data;
}
function notice(message, error = false) {
  const box = $('#notice'); box.textContent = message; box.classList.toggle('error', error); box.hidden = false;
}
function node(tag, text, className) {
  const element = document.createElement(tag);
  if (text != null) element.textContent = text;
  if (className) element.className = className;
  return element;
}
function safeImage(value) {
  try { const url = new URL(value || '/images/category.svg', location.href); return ['http:', 'https:'].includes(url.protocol) ? url.href : '/images/category.svg'; }
  catch { return '/images/category.svg'; }
}
function actions(kind, item) {
  const box = node('div', null, 'actions');
  const edit = node('button', 'Sửa'); edit.type = 'button'; edit.setAttribute('aria-label', `Sửa ${item.title || item.name}`);
  edit.addEventListener('click', () => kind === 'Product' ? showProduct(item) : showCategory(item));
  const remove = node('button', 'Xóa', 'danger'); remove.type = 'button'; remove.setAttribute('aria-label', `Xóa ${item.title || item.name}`);
  remove.addEventListener('click', () => { pendingDelete = { kind, id: item.id }; $('#delete-description').textContent = item.title || item.name; clearError($('#delete-form')); $('#delete-dialog').showModal(); });
  box.append(edit, remove); return box;
}
function emptyRow(body, columns, message) { const row = node('tr'); const cell = node('td', message, 'empty'); cell.colSpan = columns; row.append(cell); body.append(row); }
function renderProducts(items) {
  filteredProducts = items;
  const body = $('#products-body'); body.replaceChildren();
  $('#result-count').textContent = `${items.length} sản phẩm`;
  if (!items.length) return emptyRow(body, 6, 'Chưa có sản phẩm. Hãy thêm sản phẩm đầu tiên.');
  for (const p of items) {
    const row = node('tr'), name = node('td');
    name.append(node('span', p.title, 'product-name'), node('span', p.desc || `Mã sản phẩm #${p.id}`, 'description'));
    const category = node('td'); category.append(node('span', p.category.name, 'badge'));
    const stock = node('td'); stock.append(node('span', p.quantity, `stock${p.quantity === 0 ? ' zero' : ''}`));
    const buttons = node('td'); buttons.append(actions('Product', p));
    row.append(name, category, node('td', money.format(p.price), 'price'), stock, node('td', p.user.fullname), buttons); body.append(row);
  }
}
function renderCategories() {
  const body = $('#categories-body'); body.replaceChildren();
  if (!state.categories.length) return emptyRow(body, 4, 'Chưa có danh mục. Hãy tạo một danh mục mới.');
  for (const c of state.categories) {
    const row = node('tr'), first = node('td'), wrapper = node('div', null, 'category-cell');
    const img = node('img'); img.src = safeImage(c.images); img.alt = ''; img.loading = 'lazy';
    img.addEventListener('error', () => { img.src = '/images/category.svg'; }, { once: true });
    wrapper.append(img, node('span', c.name, 'product-name')); first.append(wrapper);
    const buttons = node('td'); buttons.append(actions('Category', c));
    row.append(first, node('td', c.users.map(u => u.fullname).join(', ') || 'Chưa liên kết'), node('td', c.products.length), buttons); body.append(row);
  }
}
function options(select, items, label, placeholder) {
  select.replaceChildren(); if (placeholder) select.append(new Option(placeholder, ''));
  for (const item of items) select.append(new Option(item[label], item.id));
}
async function filterProducts() {
  const request = ++filterRequest, id = $('#category-filter').value;
  $('#products-body').replaceChildren(); emptyRow($('#products-body'), 6, 'Đang tải dữ liệu…');
  try {
    const items = id ? (await graphql(`query($id: ID!) { productsByCategory(categoryId: $id) { ${productFields} } }`, { id })).productsByCategory : state.products;
    if (request === filterRequest) renderProducts(items);
  } catch (error) { if (request === filterRequest) { $('#products-body').replaceChildren(); emptyRow($('#products-body'), 6, 'Không tải được sản phẩm. Hãy thử làm mới.'); notice(error.message, true); } }
}
async function load() {
  const button = $('#refresh'); button.disabled = true;
  try {
    const data = await graphql(`{ products { ${productFields} } categories { id name images users { id fullname } products { id } } users { id fullname email } }`);
    state = data;
    $('#product-count').textContent = state.products.length;
    $('#category-count').textContent = state.categories.length;
    $('#stock-count').textContent = state.products.reduce((sum, p) => sum + p.quantity, 0).toLocaleString('vi-VN');
    const selected = $('#category-filter').value;
    options($('#category-filter'), state.categories, 'name', 'Tất cả danh mục');
    $('#category-filter').value = state.categories.some(c => c.id === selected) ? selected : '';
    renderCategories(); await filterProducts();
    return true;
  } catch (error) { notice(error.message, true); return false; }
  finally { button.disabled = false; }
}
function clearError(form) { const error = form.querySelector('.form-error'); error.textContent = ''; error.hidden = true; }
function showProduct(product) {
  if (!state.categories.length || !state.users.length) return notice('Cần có danh mục và người dùng trước khi thêm sản phẩm.', true);
  const form = $('#product-form'); form.reset(); clearError(form);
  options(form.elements.categoryId, state.categories, 'name'); options(form.elements.userId, state.users, 'fullname');
  form.elements.id.value = product?.id || '';
  $('#product-heading').textContent = product ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm';
  if (product) {
    for (const key of ['title', 'quantity', 'price', 'desc']) form.elements[key].value = product[key] ?? '';
    form.elements.categoryId.value = product.category.id; form.elements.userId.value = product.user.id;
  } else { form.elements.quantity.value = 0; form.elements.price.value = 0; }
  $('#product-dialog').showModal();
}
function showCategory(category) {
  const form = $('#category-form'); form.reset(); clearError(form);
  form.elements.id.value = category?.id || ''; form.elements.name.value = category?.name || ''; form.elements.images.value = category?.images || '';
  $('#category-heading').textContent = category ? 'Cập nhật danh mục' : 'Thêm danh mục';
  const box = $('#category-users'); box.replaceChildren();
  for (const user of state.users) {
    const label = node('label'), check = node('input'); check.type = 'checkbox'; check.name = 'userIds'; check.value = user.id;
    check.checked = !!category?.users.some(u => u.id === user.id); label.append(check, node('span', user.fullname)); box.append(label);
  }
  $('#category-dialog').showModal();
}
async function submit(form, work, success) {
  clearError(form); const button = form.querySelector('[type=submit]'); button.disabled = true;
  try { await work(); form.closest('dialog').close(); if (await load()) notice(success); }
  catch (error) { const box = form.querySelector('.form-error'); box.textContent = error.message; box.hidden = false; }
  finally { button.disabled = false; }
}
$('#product-form').addEventListener('submit', event => {
  event.preventDefault(); const form = event.currentTarget, f = form.elements, id = f.id.value;
  const input = { title: f.title.value.trim(), price: Number(f.price.value), quantity: Number(f.quantity.value), desc: f.desc.value.trim(), categoryId: f.categoryId.value, userId: f.userId.value };
  submit(form, () => graphql(id ? 'mutation($id: ID!, $input: ProductInput!) { updateProduct(id: $id, input: $input) { id } }' : 'mutation($input: ProductInput!) { createProduct(input: $input) { id } }', { id: id || null, input }), 'Đã lưu sản phẩm.');
});
$('#category-form').addEventListener('submit', event => {
  event.preventDefault(); const form = event.currentTarget, f = form.elements, id = f.id.value;
  const input = { name: f.name.value.trim(), images: f.images.value.trim(), userIds: Array.from(form.querySelectorAll('[name=userIds]:checked'), c => c.value) };
  submit(form, () => graphql(id ? 'mutation($id: ID!, $input: CategoryInput!) { updateCategory(id: $id, input: $input) { id } }' : 'mutation($input: CategoryInput!) { createCategory(input: $input) { id } }', { id: id || null, input }), 'Đã lưu danh mục.');
});
$('#delete-form').addEventListener('submit', event => { event.preventDefault(); if (pendingDelete) submit(event.currentTarget, () => graphql(`mutation($id: ID!) { delete${pendingDelete.kind}(id: $id) }`, { id: pendingDelete.id }), 'Đã xóa thành công.'); });
for (const button of document.querySelectorAll('[data-close]')) button.addEventListener('click', () => document.getElementById(button.dataset.close).close());
function setTab(kind) {
  for (const value of ['product', 'category']) { const active = kind === value; $(`#${value}-tab`).setAttribute('aria-selected', active); $(`#${value === 'product' ? 'products' : 'categories'}-panel`).hidden = !active; }
}
$('#product-tab').addEventListener('click', () => setTab('product'));
$('#category-tab').addEventListener('click', () => setTab('category'));
$('#new-product').addEventListener('click', () => showProduct());
$('#new-category').addEventListener('click', () => showCategory());
$('#category-filter').addEventListener('change', filterProducts);
$('#refresh').addEventListener('click', async () => { if (await load()) notice('Dữ liệu đã được làm mới.'); });
load();
