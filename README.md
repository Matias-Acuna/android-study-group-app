# DisciteOmnes – Collaborative Study App

**DisciteOmnes** is a mobile Android app designed to help students organize study groups, collaborate on tasks, and stay productive. This project combines a Java-based Android frontend with a Node.js backend providing a RESTful API for user, group, and task management.

## 🔧 Features

- ✅ User authentication with Firebase Auth (register & login)
- ✅ Create or join study groups
- ✅ Manage tasks within groups (add, update, complete)
- ✅ JWT-secured backend API
- ✅ Clean UI following Material Design principles

## 🧱 Project Structure

### Android App (Frontend)
- Built with **Android Studio** in Java
- UI Components: `ConstraintLayout`, `RecyclerView`, `Fragments`, `TabLayout`, `MaterialButton`
- Network: Retrofit for HTTP communication
- Token handling: SharedPreferences for JWT persistence

### Backend API (Node.js)
- Based on **Express.js**
- Simulated database using `.json` files (users, groups, tasks)
- Auth with JWT tokens
- Endpoints:

| Method | Endpoint | Function |
|--------|----------|----------|
| POST   | /auth/register | Register new user |
| POST   | /auth/login    | User login |
| GET    | /groups         | List all groups |
| POST   | /groups         | Create a new group |
| POST   | /groups/:id/join | Join a group |
| POST   | /groups/:id/leave | Leave a group |
| DELETE | /groups/:id    | Delete group |
| GET    | /groups/:id/tasks | Get group tasks |
| POST   | /groups/:id/tasks | Add new task |
| PUT    | /tasks/:id     | Update task status |

## 📦 Tools & Libraries Used

- **Android Studio (Java)**
- **Firebase Auth**
- **Retrofit**
- **Node.js + Express.js**
- **JSON for data simulation**
- **GitHub for version control**

## 📁 Backend Folder Structure

```
/data/              → JSON files (users.json, groups.json, tasks.json)
/routes/            → auth.js, groups.js, tasks.js
/middleware/        → authMiddleware.js
db.js               → read/write helpers for JSON
index.js            → app entry point
```

## 🚀 Getting Started

### Backend

```bash
npm install <---- dependencies necessaries 
node index.js
npm start <----- to run the backend 
```

Server will run on: `http://localhost:8080`

### Android App

1. Clone project in Android Studio
2. Sync Gradle
3. Replace your Firebase config if needed
4. Run on emulator or real device

## 🤝 Contributing

This project was built as part of a university course on mobile development. You are welcome to fork it, experiment, and build upon it.

