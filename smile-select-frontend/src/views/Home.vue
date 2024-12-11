<template>
  <div id="home">
    <header class="home-header">
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

      <!-- AboutUs Component -->
      <AboutUsComponent />

      <section id="contact" class="contact-section">
        <h2>Contact Us</h2>
        <p>Email: info@smileselect.com</p>
        <p>Phone: +46 31-20 20 20</p>
        <p>Address: 123 Smile St, Happy Town</p>
      </section>
    </main>

    <footer class="home-footer">
      <p>&copy; 2024 Smile Select. All rights reserved.</p>
    </footer>
  </div>
</template>

<script>
import AboutUsComponent from '@/components/AboutUsComponent.vue';
export default {
  name: "HomeView",
  components: {
    AboutUsComponent
  },
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

#home {
  text-align: center;
}

.home-header {
  background-color: #206050;
  color: white;
  padding: 10px 10px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.home-header nav ul {
  list-style: none;
  padding: 0;
  display: flex;
  gap: 20px;
}

.home-header nav a {
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
  padding: 50px 20px;
  background: url('../../public/images/wavyBackground.png') no-repeat center top;
  background-size: cover;
  color: white;
  text-align: center;
  overflow-x: hidden;
}

.about-cards {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  scroll-snap-type: x mandatory;
  padding: 20px;
  margin-top: 20px;
}

.about-card {
  flex: 0 0 auto;
  min-width: 300px;
  max-width: 300px;
  background-color: rgba(255, 255, 255, 0.8);
  color: #000;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  scroll-snap-align: start;
}

.about-card p {
  margin: 0;
}

.contact-section {
  padding: 100px;
  max-width: 100vw;
  margin: 0;
  color: Black;
  background: white;
}



.home-footer {
  background-color: #206050;
  color: white;
  padding: 5px;
  position: fixed;
  width: 100%;
  bottom: 0;
}
</style>