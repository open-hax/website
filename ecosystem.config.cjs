// pm2 process definitions for local development.
//
// Both processes write into dist/site, the single build output directory the
// dev server and a deployment both serve. Run `pnpm prepare:dev` first: it
// stages assets, generates the gallery manifest and writes the per-locale HTML
// shells, none of which the watchers produce.
const cwd = __dirname;

module.exports = {
  apps: [
    {
      name: "website-css",
      script: "pnpm",
      args: ["exec", "tailwindcss", "-c", "tailwind.config.ts", "-i", "src/index.css", "-o", "dist/site/app.css", "--watch"],
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
