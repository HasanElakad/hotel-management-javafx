# Hotel Management System 🏨

**Production JavaFX Desktop App** - Complete hotel operations: Login → Dashboard → Room Booking → Reservations Management. MaterialFX UI, MySQL backend. **NetBeans 28, JDK 25, JavaFX 25**.

## 🎯 Live Demo Flow
Login (recept/pass123) →
Dashboard (4 Room Cards: Single/Double/Triple/Suite) →
View Rooms Table → Guest Form (SSN≥14 chars) →
Auto-price Calc → Reservations Table (Stats/Extend/Cancel)


## ✨ Features
- **Auth**: Username/password → Role dashboard
- **Rooms**: Single/Double/Triple/Suite filtering
- **Booking**: Guest validation + nights×price calc (extra bed ×1.1)
- **Reservations**: TableView stats + extend/cancel
- **Validation**: SSN≥14 chars, phone≥11, valid email/dates
- **Status**: Available/Reserved/Occupied/Cleaning/Maintenance

## 🛠 Tech Stack
| Component | Version |
|-----------|---------|
| JDK | 25 |
| JavaFX | 25 |
| NetBeans | 28 |
| MySQL | Remote (Aiven) |
| MaterialFX | MFXButton/TableView |
| Maven | Build system |

## 📁 Project Structure
src/main/java/com/hotel/management/javafx/
├── App.java (FXML scene manager)
├── controller/
│ ├── LoginController.java
│ ├── DashboardController.java
│ ├── ReservationsController.java
│ └── RoomReservationController.java
├── db/
│ ├── DatabaseConnection.java
│ ├── UserDAO.java
│ ├── RoomDAO.java
│ └── ReservationDAO.java
└── model/
├── User.java
├── Guest.java
├── Room.java
└── Reservation.java

resources/
├── login.fxml
├── dashboard.fxml
├── reservations.fxml
├── RoomReservationView.fxml
├── styles.css
└── database.properties (.gitignore)


## 🚀 Quick Setup
```bash
git clone https://github.com/HasanElakad/hotel-management-javafx.git
# NetBeans 28 → JDK 25 → mvn clean javafx:run
```

## 🗄️ Database Schema (EXACT from ReservationDAO)
sql


CREATE DATABASE hotel_db;
USE hotel_db;

-- Users (LoginController)
CREATE TABLE users (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) DEFAULT 'receptionist'
);

-- Rooms (RoomDAO)
CREATE TABLE rooms (
  id INT PRIMARY KEY AUTO_INCREMENT,
  room_number VARCHAR(10) UNIQUE NOT NULL,
  capacity INT,
  room_type VARCHAR(20),
  price DECIMAL(10,2),
  floor INT,
  extra_bed BOOLEAN DEFAULT FALSE,
  status VARCHAR(20) DEFAULT 'Available'
);

-- Reservations (EXACT ReservationDAO.addReservation() columns)
CREATE TABLE reservations (
  reservation_id VARCHAR(255) PRIMARY KEY,
  guest_ssn VARCHAR(20),
  guest_name VARCHAR(100),
  guest_phone VARCHAR(20),
  guest_email VARCHAR(100),
  room_id VARCHAR(10),
  check_in DATE,
  check_out DATE,
  total_price DECIMAL(10,2),
  status VARCHAR(20) DEFAULT 'Reserved',
  is_paid BOOLEAN DEFAULT FALSE
);

-- Test Data
INSERT INTO users VALUES (1,'recept','pass123','receptionist');
INSERT INTO rooms VALUES 
(1,'101',1,'Single',75.00,1,FALSE,'Available'),
(2,'102',2,'Double',120.00,1,TRUE,'Available'),
(3,'201',3,'Triple',150.00,2,FALSE,'Occupied'),
(4,'301',4,'Suite',220.00,3,TRUE,'Reserved');


## 🔌 database.properties (NEVER COMMIT)
text
db.host=your-mysql-host
db.port=3306
db.name=hotel_db
db.user=avnadmin
db.password=your_token

## 💰 Pricing Formula
java
total = nights × room.price × (extra_bed ? 1.1 : 1.0)

## 🎮 Usage
text
1. Login (recept/pass123)
2. Dashboard → "View Single Rooms" 
3. Select room → Fill guest form → Pick dates
4. Auto-price → Confirm → "Reserved" status
5. Reservations → Extend/Cancel actions

## 📱 UI Screens
1. login.fxml: Clean login form
2. dashboard.fxml: 2×2 room cards w/ MaterialFX
3. reservations.fxml: Stats + Reservations TableView
4. RoomReservationView.fxml: Room selection + booking dialog

## 🤝 Contributing
bash
git checkout -b feature/new-feature
mvn clean javafx:run
git push origin feature/new-feature
