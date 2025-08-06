# Habito

Habito is a cross-platform habit tracking solution, featuring a FastAPI/PostgreSQL backend and an Android mobile app. It helps users build and maintain positive habits through intuitive interfaces, robust APIs, and advanced features like photo verification and streak tracking.

---

## Table of Contents

- [Habito Android App](#habito-android-app)
- [Habito Backend](#habito-backend)
- [Setup and Run the Backend](#setup-and-run-the-backend)
  - [Option 1: Run Locally (Without Docker)](#option-1-run-locally-without-docker)
  - [Option 2: Run with Docker (Recommended)](#option-2-run-with-docker-recommended)
- [Screenshots](#screenshots)
- [Permissions](#permissions)
- [Licensing](#licensing)

---

## Habito Android App

Habito's Android app is designed to help users build and maintain positive habits through an engaging interface. Key features include:

- **User-Friendly Interface**: Easily define, manage, and track multiple habits.
- **Daily Reminders**: Stay consistent with timely notifications.
- **Photo Verification**: Validate habit completion with advanced machine learning for image analysis.
- **Streak Tracking**: Visualize progress and maintain streaks.
- **Leaderboards**: Compare progress with others for healthy competition.
- **Data Privacy**: Real-time photo verification without storing sensitive data.
- **Optimized for Android**: Seamless performance tailored for Android devices.

---

## Habito Backend

Habito Backend is a FastAPI and PostgreSQL-based backend for the Habito app, providing robust APIs to manage and interact with application data.

---

## Setup and Run the Backend

There are two ways to run the backend application:

### Option 1: Run Locally (Without Docker)

1. **Clone the Repository**
    ```bash
    git clone https://github.com/SaranshBaniyal/habito-backend.git
    cd habito-backend
    ```

2. **Create and Activate a Virtual Environment**
    ```bash
    # On macOS/Linux
    python3 -m venv venv
    source venv/bin/activate

    # On Windows
    python -m venv venv
    venv\Scripts\activate
    ```

3. **Install Requirements**
    ```bash
    pip install -r requirements.txt
    ```

4. **Setup the Database**
    Ensure PostgreSQL and PostGIS are installed and running. Create tables using the `.sql` files in the `db_schemas` directory:
    ```bash
    psql -U <your_username> -d <your_database> -f db_schemas/<schema_file>.sql
    ```
    Repeat for each `.sql` file.

    *To install PostGIS on Ubuntu:*
    ```bash
    sudo apt-get install postgresql-<VERSION_NUMBER>-postgis-3
    ```

5. **Configure Environment Variables**
    ```bash
    cp .env-example .env
    ```
    Edit `.env` with your database credentials and settings.

6. **Run the Application**
    ```bash
    uvicorn main:app --workers 4 --host 0.0.0.0 --port 8000
    ```

---

### Option 2: Run with Docker (Recommended)

1. **Build and Start the Containers**
    ```bash
    docker-compose up --build
    ```
    - FastAPI backend on port 8008
    - PostgreSQL on port 5433

2. **Start the Containers (Subsequent Runs)**
    ```bash
    docker-compose up
    ```

3. **Stopping the Containers**
    ```bash
    docker-compose down
    # To also remove volumes:
    docker-compose down --volumes
    ```

---

## Screenshots

<img src="https://github.com/user-attachments/assets/9e82ae87-46c5-4a23-91f6-1b7bed0dbe53" alt="Screenshot 1" width="300">
<img src="https://github.com/user-attachments/assets/9ca95d59-1bcb-4770-a14a-9f04c76a19db" alt="Screenshot 2" width="300">
<img src="https://github.com/user-attachments/assets/9c935aa4-2144-477f-8cfa-8c165e38a611" alt="Screenshot 3" width="300">
<img src="https://github.com/user-attachments/assets/36da2557-682d-4e16-bdf8-fd4372f5e5e4" alt="Screenshot 4" width="300">
<img src="https://github.com/user-attachments/assets/1a55c98c-7b08-4b55-8a97-21f8232b47a4" alt="Screenshot 5" width="300">

---

## Permissions

The Android app requires:
- **Camera**: To capture photos for habit verification.
- **Storage**: To access and process images temporarily for verification.
- **Notifications**: To send daily habit reminders.

---

## Licensing

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

---

Start tracking your habits and achieve your goals with **Habito** today!