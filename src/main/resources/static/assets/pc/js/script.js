let particles = [];
let frequency = 50;
setInterval(
function () {
  popolate();
}.bind(this),
frequency);



let tela = document.createElement('canvas');
tela.width = $(window).width();
tela.height = $(window).height();
$("body").append(tela);

let canvas = tela.getContext('2d');

class Particle {
  constructor(canvas, options) {
    let random = Math.random();
    this.canvas = canvas;

    this.x = options.x;
    this.y = options.y;
    this.s = 0.5 + Math.random();
    this.a = 0;
    this.w = $(window).width();
    this.h = $(window).height();
    this.radius = options.radius || 0.5 + Math.random() * 10;
    this.color = options.color || "#E5483F";

    setTimeout(function () {
      if (this.radius > 0.5) {
        particles.push(
        new Particle(canvas, {
          x: this.x,
          y: this.y,
          color: this.radius / 2 > 1 ? "#d6433b" : "#FFFFFF",
          radius: this.radius / 2.2 }));


      }
    }.bind(this), 3000);
  }

  render() {
    this.canvas.beginPath();
    this.canvas.arc(this.x, this.y, this.radius, 0, 2 * Math.PI);
    this.canvas.lineWidth = 2;
    this.canvas.fillStyle = this.color;
    this.canvas.fill();
    this.canvas.closePath();
  }

  swapColor() {
    if (this.color != "#FFFFFF") {
      if (this.x < this.w / 2) {
        this.color = "#36fcfa";
      } else {
        this.color = "#E5483F";
      }
    }
  }

  move() {
    this.swapColor();
    this.x += Math.cos(this.a) * this.s;
    this.y += Math.sin(this.a) * this.s;
    this.a += Math.random() * 0.8 - 0.4;

    if (this.x < 0 || this.x > this.w - this.radius) {
      return false;
    }

    if (this.y < 0 || this.y > this.h - this.radius) {
      return false;
    }
    this.render();
    return true;
  }}


/*
      * Function to clear layer canvas
      * @num:number number of particles
      */
function popolate() {
  particles.push(
  new Particle(canvas, {
    x: $(window).width() / 2,
    y: $(window).height() / 2 }));


  return particles.length;
}

function clear() {
  canvas.globalAlpha = 0.04;
  canvas.fillStyle = '#000042';
  canvas.fillRect(0, 0, tela.width, tela.height);
  canvas.globalAlpha = 1;
}

function update() {
  particles = particles.filter(function (p) {
    return p.move();
  });
  clear();
  requestAnimationFrame(update.bind(this));
}
update();