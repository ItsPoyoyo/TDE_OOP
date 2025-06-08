// Dashboard related JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // DOM elements
    const usernameDisplay = document.getElementById('username-display');
    const logoutBtn = document.getElementById('logout-btn');
    const categoriesList = document.getElementById('categories-list');
    const addCategoryBtn = document.getElementById('add-category-btn');
    const newCategoryInput = document.getElementById('new-category');
    const searchInput = document.getElementById('search-input');
    const searchBtn = document.getElementById('search-btn');
    const newNoteBtn = document.getElementById('new-note-btn');
    const createFirstNoteBtn = document.getElementById('create-first-note-btn');
    const notesContainer = document.getElementById('notes-container');
    const emptyState = document.getElementById('empty-state');
    
    // Modal elements
    const noteModal = document.getElementById('note-modal');
    const modalTitle = document.getElementById('modal-title');
    const noteForm = document.getElementById('note-form');
    const noteIdInput = document.getElementById('note-id');
    const noteTitleInput = document.getElementById('note-title');
    const noteCategorySelect = document.getElementById('note-category');
    const noteContentInput = document.getElementById('note-content');
    const noteTagsInput = document.getElementById('note-tags');
    const noteFavoriteInput = document.getElementById('note-favorite');
    const cancelNoteBtn = document.getElementById('cancel-note-btn');
    const closeModalBtn = document.querySelector('.close-modal');
    
    // State variables
    let currentCategory = 'all';
    let notes = [];
    let categories = [];
    
    // Initialize
    init();
    
    // Event listeners
    logoutBtn.addEventListener('click', logout);
    addCategoryBtn.addEventListener('click', addCategory);
    searchBtn.addEventListener('click', searchNotes);
    newNoteBtn.addEventListener('click', openNewNoteModal);
    createFirstNoteBtn.addEventListener('click', openNewNoteModal);
    cancelNoteBtn.addEventListener('click', closeModal);
    closeModalBtn.addEventListener('click', closeModal);
    noteForm.addEventListener('submit', saveNote);
    
    // Close modal when clicking outside
    window.addEventListener('click', function(event) {
        if (event.target === noteModal) {
            closeModal();
        }
    });
    
    // Initialize the dashboard
    function init() {
        // Get username from session
        fetch('api/auth/user')
            .then(response => {
                if (!response.ok) {
                    // Redirect to login if not authenticated
                    window.location.href = 'login.html';
                    return;
                }
                return response.json();
            })
            .then(data => {
                if (data && data.username) {
                    usernameDisplay.textContent = data.username;
                }
            })
            .catch(error => {
                console.error('Error fetching user data:', error);
                window.location.href = 'login.html';
            });
        
        // Load notes
        loadNotes();
        
        // Add event listeners to category links
        document.addEventListener('click', function(e) {
            if (e.target.matches('.sidebar-menu a')) {
                e.preventDefault();
                const category = e.target.getAttribute('data-category');
                setActiveCategory(category);
                loadNotes(category);
            }
        });
    }
    
    // Load notes from server
    function loadNotes(category = 'all') {
        let url = 'api/notes';
        
        if (category === 'favorites') {
            url = 'api/notes/favorites';
        } else if (category !== 'all') {
            url = `api/notes/category/${category}`;
        }
        
        fetch(url)
            .then(response => response.json())
            .then(data => {
                notes = data;
                renderNotes(notes);
                updateCategories(notes);
            })
            .catch(error => {
                console.error('Error loading notes:', error);
            });
    }
    
    // Render notes in the container
    function renderNotes(notes) {
        if (notes.length === 0) {
            notesContainer.innerHTML = '';
            emptyState.style.display = 'block';
            return;
        }
        
        emptyState.style.display = 'none';
        notesContainer.innerHTML = '';
        
        notes.forEach(note => {
            const noteCard = document.createElement('div');
            noteCard.className = 'note-card';
            noteCard.innerHTML = `
                <div class="note-card-actions">
                    <button class="favorite-btn ${note.isFavorite ? 'active' : ''}" data-id="${note.id}">★</button>
                    <button class="edit-btn" data-id="${note.id}">✎</button>
                    <button class="delete-btn" data-id="${note.id}">✕</button>
                </div>
                <h3>${note.title}</h3>
                <div class="note-card-category">${note.category || 'Sem categoria'}</div>
                <div class="note-card-content">${note.content || ''}</div>
                <div class="note-card-footer">
                    <span>Atualizado: ${formatDate(note.updatedAt)}</span>
                </div>
            `;
            
            notesContainer.appendChild(noteCard);
            
            // Add event listeners to buttons
            const favoriteBtn = noteCard.querySelector('.favorite-btn');
            const editBtn = noteCard.querySelector('.edit-btn');
            const deleteBtn = noteCard.querySelector('.delete-btn');
            
            favoriteBtn.addEventListener('click', () => toggleFavorite(note.id));
            editBtn.addEventListener('click', () => openEditNoteModal(note.id));
            deleteBtn.addEventListener('click', () => deleteNote(note.id));
        });
    }
    
    // Update categories list based on notes
    function updateCategories(notes) {
        // Extract unique categories
        const uniqueCategories = [...new Set(notes.map(note => note.category).filter(Boolean))];
        
        // Update categories list
        categories = uniqueCategories;
        
        // Clear existing category options (except default ones)
        const defaultCategories = ['all', 'favorites'];
        Array.from(categoriesList.children).forEach(li => {
            const category = li.querySelector('a').getAttribute('data-category');
            if (!defaultCategories.includes(category)) {
                li.remove();
            }
        });
        
        // Add category options
        categories.forEach(category => {
            const li = document.createElement('li');
            li.innerHTML = `<a href="#" data-category="${category}">${category}</a>`;
            categoriesList.appendChild(li);
        });
        
        // Update category select in modal
        noteCategorySelect.innerHTML = '<option value="">Sem categoria</option>';
        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            noteCategorySelect.appendChild(option);
        });
    }
    
    // Set active category
    function setActiveCategory(category) {
        currentCategory = category;
        
        // Update active class
        document.querySelectorAll('.sidebar-menu a').forEach(a => {
            a.classList.remove('active');
            if (a.getAttribute('data-category') === category) {
                a.classList.add('active');
            }
        });
    }
    
    // Add new category
    function addCategory() {
        const category = newCategoryInput.value.trim();
        
        if (!category) {
            return;
        }
        
        if (!categories.includes(category)) {
            categories.push(category);
            
            // Add to sidebar
            const li = document.createElement('li');
            li.innerHTML = `<a href="#" data-category="${category}">${category}</a>`;
            categoriesList.appendChild(li);
            
            // Add to select
            const option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            noteCategorySelect.appendChild(option);
            
            // Clear input
            newCategoryInput.value = '';
        }
    }
    
    // Search notes
    function searchNotes() {
        const query = searchInput.value.trim();
        
        if (!query) {
            loadNotes(currentCategory);
            return;
        }
        
        fetch(`api/notes/search/${query}`)
            .then(response => response.json())
            .then(data => {
                notes = data;
                renderNotes(notes);
            })
            .catch(error => {
                console.error('Error searching notes:', error);
            });
    }
    
    // Toggle favorite status
    function toggleFavorite(noteId) {
        fetch(`api/notes/${noteId}/favorite`, {
            method: 'PUT'
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'success') {
                    loadNotes(currentCategory);
                }
            })
            .catch(error => {
                console.error('Error toggling favorite:', error);
            });
    }
    
    // Open modal for new note
    function openNewNoteModal() {
        modalTitle.textContent = 'Nova Anotação';
        noteIdInput.value = '';
        noteTitleInput.value = '';
        noteCategorySelect.value = '';
        noteContentInput.value = '';
        noteTagsInput.value = '';
        noteFavoriteInput.checked = false;
        
        noteModal.style.display = 'flex';
    }
    
    // Open modal for editing note
    function openEditNoteModal(noteId) {
        const note = notes.find(n => n.id === noteId);
        
        if (!note) {
            return;
        }
        
        modalTitle.textContent = 'Editar Anotação';
        noteIdInput.value = note.id;
        noteTitleInput.value = note.title;
        noteCategorySelect.value = note.category || '';
        noteContentInput.value = note.content || '';
        noteFavoriteInput.checked = note.isFavorite;
        
        // Load tags
        fetch(`api/notes/${noteId}/tags`)
            .then(response => response.json())
            .then(data => {
                const tagNames = data.map(tag => tag.name).join(', ');
                noteTagsInput.value = tagNames;
            })
            .catch(error => {
                console.error('Error loading tags:', error);
            });
        
        noteModal.style.display = 'flex';
    }
    
    // Close modal
    function closeModal() {
        noteModal.style.display = 'none';
    }
    
    // Save note
    function saveNote(e) {
        e.preventDefault();
        
        const noteId = noteIdInput.value;
        const title = noteTitleInput.value;
        const category = noteCategorySelect.value;
        const content = noteContentInput.value;
        const tags = noteTagsInput.value.split(',').map(tag => tag.trim()).filter(Boolean);
        const isFavorite = noteFavoriteInput.checked;
        
        if (!title) {
            alert('Por favor, insira um título para a anotação.');
            return;
        }
        
        const noteData = {
            title,
            content,
            category,
            isFavorite,
            tags
        };
        
        let url = 'api/notes';
        let method = 'POST';
        
        if (noteId) {
            url = `api/notes/${noteId}`;
            method = 'PUT';
            noteData.id = noteId;
        }
        
        fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(noteData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'success') {
                    closeModal();
                    loadNotes(currentCategory);
                } else {
                    alert(data.message || 'Erro ao salvar anotação.');
                }
            })
            .catch(error => {
                console.error('Error saving note:', error);
                alert('Erro ao salvar anotação. Tente novamente mais tarde.');
            });
    }
    
    // Delete note
    function deleteNote(noteId) {
        if (!confirm('Tem certeza que deseja excluir esta anotação?')) {
            return;
        }
        
        fetch(`api/notes/${noteId}`, {
            method: 'DELETE'
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'success') {
                    loadNotes(currentCategory);
                } else {
                    alert(data.message || 'Erro ao excluir anotação.');
                }
            })
            .catch(error => {
                console.error('Error deleting note:', error);
                alert('Erro ao excluir anotação. Tente novamente mais tarde.');
            });
    }
    
    // Logout
    function logout() {
        fetch('api/auth/logout', {
            method: 'POST'
        })
            .then(() => {
                window.location.href = 'login.html';
            })
            .catch(error => {
                console.error('Error logging out:', error);
                window.location.href = 'login.html';
            });
    }
    
    // Format date
    function formatDate(dateString) {
        if (!dateString) return '';
        
        const date = new Date(dateString);
        return date.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }
});
