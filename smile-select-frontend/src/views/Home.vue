<template>
  <div id="app">
    <header class="app-header">
      <div class="header-content">
        <h1>Smile Select</h1>
        <nav>
          <ul>
            <li><a href="#home">Home</a></li>
            <li><a href="">Admin Tool</a></li>
            <li><a href="#about">About Us</a></li>
            <li><a href="#contact">Contact</a></li>
          </ul>
        </nav>
      </div>
    </header>

    <main>
      <section id="home" class="hero-section">
        <div class="hero-half" v-for="(image, index) in images" :key="index" :style="{ backgroundImage: `url(${image.src})` }">
          <div class="hero-content">
            <h2>{{ image.title }}</h2>
            <p>{{ image.description }}</p>
            <button @click="image.action">{{ image.buttonText }}</button>
          </div>
        </div>
      </section>

      <section id="about" class="about-section">
        <h2>About Us</h2>
        <p>
          Welcome to <strong>Smile Select</strong>, your trusted companion for dental care scheduling in Gothenburg. We are revolutionizing the way patients and dental clinics connect, making it easier than ever to book and manage dental appointments.
        </p>
        <p>
          With <strong>Smile Select</strong>, you gain access to a streamlined platform designed to meet the needs of both patients and dentists:
        </p>

          <p><strong>For Patients:</strong> Whether you're looking for a routine check-up or urgent dental care, Smile Select allows you to browse available time slots across multiple clinics. With just a few clicks, you can find a time that fits your schedule and book your appointment seamlessly.</p>
          <p><strong>For Dentists:</strong> Simplify your clinic’s appointment management. Create available time slots, manage patient bookings, and ensure your schedule stays organized—all from one convenient platform.</p>
        <p>
          At Smile Select, we’re proud to provide a solution that prioritizes your dental health and convenience. By connecting patients with qualified dental professionals in Gothenburg, we aim to make dental care accessible and hassle-free for everyone.
        </p>
        <p>
          <strong>Your smile matters to us.</strong> Explore Smile Select today and discover a smarter way to book dental appointments.
        </p>
      </section>

      <section id="contact" class="contact-section">
        <h2>Contact Us</h2>
        <p>Email: info@smileselect.com</p>
        <p>Phone: +46 31-20 20 20</p>
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
  data() {
    return {
      images: [
        {
          src: "/images/dentist1.jpg",
          title: "Are you a dentist?",
          description: "Manage appointments and patients",
          buttonText: "Dentist Login",
          action: () => this.navigateToLogin('DENTIST'),
        },
        {
          src: "/images/patient1.jpg",
          title: "Are you a patient?",
          description: "Get the smile you've always wanted.",
          buttonText: "Patient Login",
          action: () => this.navigateToLogin('PATIENT'),
        },
      ],
    };
  },
  methods: {
    navigateToLogin(role) {
      this.$router.push({ path: '/auth', query: { role } });
    },
  },
};
</script>

<style>
body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  background: url('../../public/images/wavyBackground.png') no-repeat center top;
  background-size: cover;
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
  display: flex;
  height: 40vh;
  overflow: hidden;
}

.hero-half {
  flex: 1;
  position: relative;
  background-size: cover;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.hero-content {
  background-color: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(10px);
  padding: 20px;
  border-radius: 8px;
  color: Black;
  text-align: center;
  border: 1px solid #ccc;
}

.hero-content button {
  padding: 10px 20px;

  background-color: #206050;
  border-radius: 8px;
  color: white;
  border: none;
  cursor: pointer;
}
.hero-content button:hover {
  background-color: #003020;
}

 .about-section {
  padding: 100px;
  max-width: 100vw;

   backdrop-filter: brightness(40%);
   color: white;
   max-height: 600px;
  }


.contact-section {
  padding: 100px;
  max-width: 100vw;
  margin: 0;
  color: Black;
  background: white;
}



.app-footer {
  background-color: #206050;
  color: white;
  padding: 5px;
  position: fixed;
  width: 100%;
  bottom: 0;
}
</style>