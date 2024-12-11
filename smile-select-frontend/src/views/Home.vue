<template>
  <div id="app">
    <header class="app-header">
      <div class="header-content">
        <h1>Smile Select</h1>
        <nav>
          <ul>
            <li><a href="#home">Home</a></li>
            <li><a href="#services">Services</a></li>
            <li><a href="#about">About Us</a></li>
            <li><a href="#contact">Contact</a></li>
          </ul>
        </nav>
      </div>
    </header>

    <main>
      <section id="home" class="hero-section">
        <div class="hero-content">
          <h2>Your Smile, Our Priority</h2>
          <p>Welcome to Smile Select, where we bring out the best in your smile. Book your appointment today!</p>
          <button @click="bookAppointment">Book Appointment</button>
        </div>
        <div class="hero-images">
          <img src="/images/dentist1.jpg" alt="Smiling person" />
        </div>
      </section>

      <section id="services" class="services-section">
        <h2>Our Services</h2>
        <ul>
          <li>Teeth Cleaning</li>
          <li>Orthodontics</li>
          <li>Cosmetic Dentistry</li>
          <li>Emergency Dental Care</li>
        </ul>
      </section>

      <section id="about" class="about-section">
        <h2>About Us</h2>
        <p>At Smile Select, we are committed to providing top-notch dental care with state-of-the-art technology and a compassionate approach.</p>
      </section>

      <section id="contact" class="contact-section">
        <h2>Contact Us</h2>
        <p>Email: info@smileselect.com</p>
        <p>Phone: +1 (555) 123-4567</p>
        <p>Address: 123 Smile St, Happy Town</p>
      </section>
    </main>

    <footer class="app-footer">
      <p>&copy; 2024 Smile Select. All rights reserved.</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: "App",
  methods: {
    bookAppointment() {
      alert("Redirecting to appointment booking page...");
      // Add routing logic here if using Vue Router
    }
  }
};
</script>

<style>
body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  text-align: center;
}

.app-header {
  background-color: #206050;
  color: white;
  padding: 10px 10px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.app-header nav ul {
  list-style: none;
  padding: 0;
  display: flex;
  gap: 20px;
}

.app-header nav a {
  color: white;
  text-decoration: none;
}

.hero-section {
  padding: 50px;
  background-color: #f0f8ff;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hero-content {
  max-width: 50%;
}

.hero-images {
  display: flex;
  gap: 20px;
}

.hero-images img {
  width: 200px;
  height: auto;
  border-radius: 10px;
}

.hero-section button {
  padding: 10px 20px;
  background-color: #206050;
  color: white;
  border: none;
  cursor: pointer;
}

.services-section, .about-section, .contact-section {
  padding: 30px;
}

.app-footer {
  background-color: #206050;
  color: white;
  padding: 10px;
  position: fixed;
  width: 100%;
  bottom: 0;
}
</style>