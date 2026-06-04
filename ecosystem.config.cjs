const path = require("path");
const cwd = __dirname;

module.exports = {
  apps: [
    {
      name: "website-css",
      script: "pnpm",
      args: ["exec", "tailwindcss", "-c", "tailwind.config.ts", "-i", "src/index.css", "-o", "dist/app.css", "--watch"],
      cwd,
      env: {
        NODE_ENV: "development",
      },
      autorestart: false,
      watch: false,
      time: true,
      kill_timeout: 3000,
    },
    {
      name: "website-shadow",
      script: "pnpm",
      args: ["exec", "shadow-cljs", "watch", "app"],
      cwd,
      env: {
        NODE_ENV: "development",
      },
      autorestart: false,
      watch: false,
      time: true,
      kill_timeout: 3000,
    },
  ],
};
