# OpenHax Website

Portfolio website for the OpenHax organization, showcasing our products, art, and music.

## Stack

- **ClojureScript** via shadow-cljs
- **Helix** (React wrapper)
- **UXX** design system and component library
- **Tailwind CSS** with UXX design tokens

## Development

```bash
# Install dependencies
pnpm install

# Start development server (CSS + shadow-cljs watcher)
pnpm dev

# Build for production
pnpm build
```

## Structure

- `src/cljs/open_hax/website/` - ClojureScript source
  - `core.cljs` - Entry point
  - `app.cljs` - Main app shell
  - `components/` - Reusable components
  - `sections/` - Page sections

## Domain

- Production: `open-hax.promethean.rest`
