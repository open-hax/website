module.exports = {
  apps: [
    {
      name: 'axxium',
      cwd: '../../orgs/open-hax/axxium',
      script: 'npx',
      args: 'shadow-cljs run server',
      instances: 1,
      autorestart: true,
      watch: false,
      max_memory_restart: '512M',
      env: {
        NODE_ENV: 'production',
      },
      env_development: {
        NODE_ENV: 'development',
      },
      log_file: './logs/axxium.log',
      out_file: './logs/axxium-out.log',
      err_file: './logs/axxium-err.log',
      log_date_format: 'YYYY-MM-DD HH:mm:ss Z',
    },
  ],
};
