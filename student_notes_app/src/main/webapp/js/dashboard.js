// Dashboard related JavaScript
document.addEventListener("DOMContentLoaded", function() {
    const usernameDisplay = document.getElementById("username-display");
    const logoutBtn = document.getElementById("logout-btn");
    const categoriesList = document.getElementById("categories-list");
    const addCategoryBtn = document.getElementById("add-category-btn");
    const newCategoryInput = document.getElementById("new-category");
    const searchInput = document.getElementById("search-input");
    const searchBtn = document.getElementById("search-btn");
    const newNoteBtn = document.getElementById("new-note-btn");
    const createFirstNoteBtn = document.getElementById("create-first-note-btn");
    const notesContainer = document.getElementById("notes-container");
    const emptyState = document.getElementById("empty-state");

    const noteModal = document.getElementById("note-modal");
    const modalTitle = document.getElementById("modal-title");
    const noteForm = document.getElementById("note-form");
    const noteIdInput = document.getElementById("note-id");
    const noteTitleInput = document.getElementById("note-title");
    const noteCategorySelect = document.getElementById("note-category");
    const noteContentInput = document.getElementById("note-content");
    const noteTagsInput = document.getElementById("note-tags");
    const noteFavoriteInput = document.getElementById("note-favorite");
    const cancelNoteBtn = document.getElementById("cancel-note-btn");
    const closeModalBtn = document.querySelector(".close-modal");

    let currentCategory = "all";
    let notes = [];
    let categories = [];

    init();

    logoutBtn.addEventListener("click", logout);
    addCategoryBtn.addEventListener("click", addCategory);
    searchBtn.addEventListener("click", searchNotes);
    newNoteBtn.addEventListener("click", openNewNoteModal);
    createFirstNoteBtn.addEventListener("click", openNewNoteModal);
    cancelNoteBtn.addEventListener("click", closeModal);
    closeModalBtn.addEventListener("click", closeModal);
    noteForm.addEventListener("submit", saveNote);

    window.addEventListener("click", function(event) {
        if (event.target === noteModal) {
            closeModal();
        }
    });

    function init() {
        fetch("api/auth/user", {
            credentials: "include"
        })
        .then(response => {
            if (!response.ok) {
                window.location.href = "login.html";
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
            console.error("Error fetching user data:", error);
            window.location.href = "login.html";
        });

        loadNotes();

        document.addEventListener("click", function(e) {
            if (e.target.matches(".sidebar-menu a")) {
                e.preventDefault();
                const category = e.target.getAttribute("data-category");
                setActiveCategory(category);
                loadNotes(category);
            }
        });
    }

    function loadNotes(category = "all") {
        let url = "api/notes";

        if (category === "favorites") {
            url = "api/notes/favorites";
        } else if (category !== "all") {
            url = `api/notes/category/${category}`;
        }

        fetch(url, {
            credentials: "include"
        })
        .then(response => response.json())
        .then(data => {
            notes = data;
            renderNotes(notes);
            updateCategories(notes);
        })
        .catch(error => {
            console.error("Error loading notes:", error);
        });
    }

    function renderNotes(notes) {
        if (notes.length === 0) {
            notesContainer.innerHTML = "";
            emptyState.style.display = "block";
            return;
        }

        emptyState.style.display = "none";
        notesContainer.innerHTML = "";

        notes.forEach(note => {
            const noteCard = document.createElement("div");
            noteCard.className = "note-card";
            noteCard.innerHTML = `
                <div class="note-card-actions">
                    <button class="favorite-btn ${note.isFavorite ? "active" : ""}" data-id="${note.id}">★</button>
                    <button class="edit-btn" data-id="${note.id}">✎</button>
                    <button class="delete-btn" data-id="${note.id}">✕</button>
                </div>
                <h3>${note.title}</h3>
                <div class="note-card-category">${note.category || "Sem categoria"}</div>
                <div class="note-card-content">${note.content || ""}</div>
                <div class="note-card-footer">
                    <span>Atualizado: ${formatDate(note.updatedAt)}</span>
                </div>
            `;

            notesContainer.appendChild(noteCard);

            const favoriteBtn = noteCard.querySelector(".favorite-btn");
            const editBtn = noteCard.querySelector(".edit-btn");
            const deleteBtn = noteCard.querySelector(".delete-btn");

            favoriteBtn.addEventListener("click", () => toggleFavorite(note.id));
            editBtn.addEventListener("click", () => openEditNoteModal(note.id));
            deleteBtn.addEventListener("click", () => deleteNote(note.id));
        });
    }

    function updateCategories(notes) {
        const uniqueCategories = [...new Set(notes.map(note => note.category).filter(Boolean))];
        categories = uniqueCategories;

        const defaultCategories = ["all", "favorites"];
        Array.from(categoriesList.children).forEach(li => {
            const category = li.querySelector("a").getAttribute("data-category");
            if (!defaultCategories.includes(category)) {
                li.remove();
            }
        });

        categories.forEach(category => {
            const li = document.createElement("li");
            li.innerHTML = `<a href="#" data-category="${category}">${category}</a>`;
            categoriesList.appendChild(li);
        });

        noteCategorySelect.innerHTML = "<option value=\"\">Sem categoria</option>";
        categories.forEach(category => {
            const option = document.createElement("option");
            option.value = category;
            option.textContent = category;
            noteCategorySelect.appendChild(option);
        });
    }

    function setActiveCategory(category) {
        currentCategory = category;
        document.querySelectorAll(".sidebar-menu a").forEach(a => {
            a.classList.remove("active");
            if (a.getAttribute("data-category") === category) {
                a.classList.add("active");
            }
        });
    }

    function addCategory() {
        const category = newCategoryInput.value.trim();
        if (!category || categories.includes(category)) return;

        categories.push(category);

        const li = document.createElement("li");
        li.innerHTML = `<a href="#" data-category="${category}">${category}</a>`;
        categoriesList.appendChild(li);

        const option = document.createElement("option");
        option.value = category;
        option.textContent = category;
        noteCategorySelect.appendChild(option);

        newCategoryInput.value = "";
    }

    function searchNotes() {
        const query = searchInput.value.trim();
        if (!query) {
            loadNotes(currentCategory);
            return;
        }

        fetch(`api/notes/search/${query}`, {
            credentials: "include"
        })
        .then(response => response.json())
        .then(data => {
            notes = data;
            renderNotes(notes);
        })
        .catch(error => {
            console.error("Error searching notes:", error);
        });
    }

    function toggleFavorite(noteId) {
        fetch(`api/notes/${noteId}/favorite`, {
            method: "PUT",
            credentials: "include"
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                loadNotes(currentCategory);
            }
        })
        .catch(error => {
            console.error("Error toggling favorite:", error);
        });
    }

    function openNewNoteModal() {
        modalTitle.textContent = "Nova Anotação";
        noteIdInput.value = "";
        noteTitleInput.value = "";
        noteCategorySelect.value = "";
        noteContentInput.value = "";
        noteTagsInput.value = "";
        noteFavoriteInput.checked = false;
        noteModal.style.display = "flex";
    }

    function openEditNoteModal(noteId) {
        const note = notes.find(n => n.id === noteId);
        if (!note) return;

        modalTitle.textContent = "Editar Anotação";
        noteIdInput.value = note.id;
        noteTitleInput.value = note.title;
        noteCategorySelect.value = note.category || "";
        noteContentInput.value = note.content || "";
        noteFavoriteInput.checked = note.isFavorite;

        fetch(`api/notes/${noteId}/tags`, {
            credentials: "include"
        })
        .then(response => response.json())
        .then(data => {
            const tagNames = data.map(tag => tag.name).join(", ");
            noteTagsInput.value = tagNames;
        })
        .catch(error => {
            console.error("Error loading tags:", error);
        });

        noteModal.style.display = "flex";
    }

    function closeModal() {
        noteModal.style.display = "none";
    }

    function saveNote(e) {
        e.preventDefault();

        const noteId = noteIdInput.value;
        const title = noteTitleInput.value;
        const category = noteCategorySelect.value;
        const content = noteContentInput.value;
        const tags = noteTagsInput.value.split(",").map(tag => tag.trim()).filter(Boolean);
        const isFavorite = noteFavoriteInput.checked;

        if (!title) {
            alert("Por favor, insira um título para a anotação.");
            return;
        }

        const noteData = {
            title,
            content,
            category,
            isFavorite,
            tags
        };

        let url = "api/notes";
        let method = "POST";

        if (noteId) {
            url = `api/notes/${noteId}`;
            method = "PUT";
            noteData.id = noteId;
        }

        fetch(url, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(noteData),
            credentials: "include"
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                closeModal();
                loadNotes(currentCategory);
            } else {
                alert(data.message || "Erro ao salvar anotação.");
            }
        })
        .catch(error => {
            console.error("Error saving note:", error);
            alert("Erro ao salvar anotação.");
        });
    }

    function deleteNote(noteId) {
        if (!confirm("Tem certeza que deseja excluir esta anotação?")) return;

        fetch(`api/notes/${noteId}`, {
            method: "DELETE",
            credentials: "include"
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                loadNotes(currentCategory);
            }
        })
        .catch(error => {
            console.error("Error deleting note:", error);
            alert("Erro ao excluir anotação.");
        });
    }

    function logout() {
        fetch("api/auth/logout", {
            method: "POST",
            credentials: "include"
        })
        .then(() => {
            window.location.href = "login.html";
        })
        .catch(error => {
            console.error("Error logging out:", error);
            window.location.href = "login.html";
        });
    }

    function formatDate(dateString) {
        if (!dateString) return "";
        const date = new Date(dateString);
        return date.toLocaleDateString("pt-BR", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        });
    }
});


