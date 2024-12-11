<template>
  <section id="about" class="about-section">
    <h2>Our Mission</h2>
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
        "<strong>Accessible Dental Care</strong><br>We believe everyone deserves easy access to quality dental care. Smile Select bridges the gap between patients and dentists.",
        "<strong>Empowering Patients</strong><br>Your dental health is in your hands. Find the right dentist and book appointments effortlessly, all in one place.",
        "<strong>Supporting Dentists</strong><br>Our platform helps dentists manage their schedules efficiently, so they can focus on what they do best—caring for patients.",
        "<strong>Innovation for Smiles</strong><br>We're committed to leveraging technology to make dental care smarter, faster, and more convenient.",
        "<strong>Local Expertise</strong><br>Serving Gothenburg with pride, Smile Select connects patients with trusted local dental professionals.",
        "<strong>Your Smile, Our Mission</strong><br>Your satisfaction and dental health drive us to deliver the best scheduling experience possible."
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

@media (min-width: 600px) {
  .about-cards {
    justify-content: center;
  }
}

</style>