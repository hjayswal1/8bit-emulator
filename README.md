# 🕹️ 8-Bit Emulator

> A custom 8-bit CPU emulator built from scratch using Java and a JavaScript frontend.

![Emulator Demo](link-to-your-gif-or-screenshot-here.gif)

## 📖 Overview
This project is a fully functional 8-bit emulator. It simulates the core components of a retro CPU, including registers, memory, an ALU (Arithmetic Logic Unit), and a fetch-decode-execute cycle. The backend engine is written in **Java**, while the UI and interactions are handled via a **JavaScript** frontend.

## ✨ Features
- **Custom Instruction Set:** Implements core 8-bit instructions (e.g., LOAD, STORE, ADD, JMP).
- **Memory Management:** Simulates RAM and ROM address spaces.
- **Interactive Frontend:** Real-time visual feedback of CPU registers, memory dumps, and execution states.
- **Step-by-Step Debugger:** Ability to step through instructions one clock cycle at a time.

## 🛠️ Tech Stack
- **Core Engine:** Java
- **Frontend:** JavaScript, HTML/CSS (Node.js)
- **Communication:** (Mention here if you used WebSockets, REST, or JNI to connect Java and JS)

## 🚀 How to Run Locally

### Prerequisites
- Java Development Kit (JDK) 11+
- Node.js & npm

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/hjayswal1/8bit-emulator.git
   cd 8bit-emulator
   ```

2. **Run the Java Backend:**
   ```bash
   javac Main.java
   java Main
   ```

3. **Start the Frontend:**
   ```bash
   cd frontend-js
   npm install
   npm start
   ```

## 🧠 What I Learned
Building this emulator provided deep insights into low-level systems programming, CPU architecture, bitwise operations, and bridging communication between a Java backend and a web-based frontend.