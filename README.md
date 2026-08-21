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

## Translation target

This site is the intended first publication target for Knoxx's contract-owned
publication pipeline. [`docs/translation-target-contract.md`](docs/translation-target-contract.md)
records what that requires of this repository — a content source, locale routing,
a reader-side manifest contract, and a build that emits one directory — and what
it deliberately does not.

## Domain

- Production: `open-hax.promethean.rest`
