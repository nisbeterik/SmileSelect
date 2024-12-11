<template>
  <section id="about" class="about-section">
    <h2>About Us</h2>
    <div class="about-cards">
      <div class="about-card" v-for="(content, index) in aboutContent" :key="index">
        <p v-html="content"></p>
      </div>
    </div>
  </section>
</template>

<script>
export default {
  name: "AboutUs",
  data() {
    return {
      aboutContent: [
        "<strong>For Patients:</strong> Whether you're looking for a routine check-up or urgent dental care, Smile Select allows you to browse available time slots across multiple clinics. With just a few clicks, you can find a time that fits your schedule and book your appointment seamlessly.",
        "<strong>For Dentists:</strong> Simplify your clinic’s appointment management. Create available time slots, manage patient bookings, and ensure your schedule stays organized—all from one convenient platform.",
        "At Smile Select, we’re proud to provide a solution that prioritizes your dental health and convenience. By connecting patients with qualified dental professionals in Gothenburg, we aim to make dental care accessible and hassle-free for everyone.",
        "<strong>Your smile matters to us.</strong> Explore Smile Select today and discover a smarter way to book dental appointments.",
        "<strong>For Patients:</strong> Whether you're looking for a routine check-up or urgent dental care, Smile Select allows you to browse available time slots across multiple clinics. With just a few clicks, you can find a time that fits your schedule and book your appointment seamlessly.",
        "<strong>For Dentists:</strong> Simplify your clinic’s appointment management. Create available time slots, manage patient bookings, and ensure your schedule stays organized—all from one convenient platform.",
        "At Smile Select, we’re proud to provide a solution that prioritizes your dental health and convenience. By connecting patients with qualified dental professionals in Gothenburg, we aim to make dental care accessible and hassle-free for everyone.",
        "<strong>Your smile matters to us.</strong> Explore Smile Select today and discover a smarter way to book dental appointments."
      ]
    };
  }
};
</script>

<style scoped>
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
  justify-content: flex-start;
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

/* Center cards if there are fewer cards than the viewport width */
@media (min-width: 600px) {
  .about-cards {
    justify-content: center;
  }
}

</style>