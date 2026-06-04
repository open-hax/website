import { readdirSync, statSync, writeFileSync } from 'fs';
import { join, relative, dirname } from 'path';

function scanDir(dir, baseDir, acc = []) {
  try {
    const entries = readdirSync(dir, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = join(dir, entry.name);
      if (entry.isDirectory()) {
        scanDir(fullPath, baseDir, acc);
      } else if (/\.(png|jpe?g|gif|webp)$/i.test(entry.name)) {
        acc.push({
          path: "/graphics/" + relative(baseDir, fullPath).replace(/\\/g, '/'),
          name: entry.name.replace(/\.[^.]+$/, ''),
          category: relative(baseDir, dirname(fullPath)).replace(/\\/g, '/') || "root"
        });
      }
    }
  } catch (e) {}
  return acc;
}

function scanMusic(dir, baseDir, acc = []) {
  try {
    const entries = readdirSync(dir, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = join(dir, entry.name);
      if (entry.isDirectory()) {
        scanMusic(fullPath, baseDir, acc);
      } else if (/\.(wav|mp3|ogg|flac)$/i.test(entry.name)) {
        acc.push({
          path: "/music/" + relative(baseDir, fullPath).replace(/\\/g, '/'),
          title: entry.name.replace(/\.[^.]+$/, '').replace(/_/g, ' '),
          artist: "OpenHax",
          folder: relative(baseDir, dirname(fullPath)).replace(/\\/g, '/') || "root",
          duration: "--:--"
        });
      }
    }
  } catch (e) {}
  return acc;
}

const graphics = scanDir('/home/err/devel/Graphics_5000/Graphics', '/home/err/devel/Graphics_5000/Graphics');
const music = scanMusic('/home/err/Music', '/home/err/Music');

function escapeStr(s) {
  return s.replace(/\\/g, '\\\\').replace(/"/g, '\\"');
}

let cljs = "(ns open-hax.website.data.assets\n  \"Auto-generated asset manifest.\"\n  (:require [clojure.string :as str]))\n\n";

cljs += "(def graphics\n  [\n";
for (const g of graphics.slice(0, 200)) {
  cljs += `    {:id ${Math.floor(Math.random()*1000000)} :title "${escapeStr(g.name)}" :category "${escapeStr(g.category)}" :src "${escapeStr(g.path)}"}\n`;
}
cljs += "    ])\n\n";

cljs += "(def music\n  [\n";
for (const m of music.slice(0, 200)) {
  cljs += `    {:id ${Math.floor(Math.random()*1000000)} :title "${escapeStr(m.title)}" :artist "${escapeStr(m.artist)}" :folder "${escapeStr(m.folder)}" :duration "${escapeStr(m.duration)}" :src "${escapeStr(m.path)}"}\n`;
}
cljs += "    ])\n";

writeFileSync('/home/err/devel/orgs/open-hax/website/src/cljs/open_hax/website/data/assets.cljs', cljs);
console.log(`Generated assets.cljs: ${graphics.length} graphics, ${music.length} music`);
