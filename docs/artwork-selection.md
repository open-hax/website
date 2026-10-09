# Personal OpenHax artwork snapshot

This snapshot restores the personal OpenHax gallery from the author's existing
`Graphics/` collection. It preserves the existing artwork and filenames rather
than generating replacement art. The originals are ordinary repository files,
so CI and container builds do not depend on a developer's home directory,
symlinks, or external media storage.

## Inventory and selection

The source inventory contains 611 regular files, totaling 8,403,603 bytes:
533 SVGs, 75 PNGs, and three documentation/text files. No source symlinks were
found. The complete source inventory is represented in
`assets/graphics-provenance.json`, including excluded files.

The staged collection contains **442 unique original artwork files**:
382 SVGs and 60 PNGs, totaling **6,313,315 bytes**. Every staged file was copied
byte-for-byte and checked against its source SHA-256. No source file was edited.

| Disposition | Count | Basis |
| --- | ---: | --- |
| Accepted | 442 | Original SVG with a valid SVG root/XML structure, or successfully decoded PNG; not otherwise excluded |
| Exact duplicate | 13 | SHA-256 equality with an accepted file; deterministic lexicographic source path retained |
| Excluded | 156 | 124 historically quarantined SVGs, 26 other structurally invalid SVGs, three screenshots, and three text/documentation files |

The full inventory has 14 exact duplicate pairs. One pair consists of malformed
or quarantined files, so those two files are excluded rather than classified
as a retained artwork plus duplicate. Filename similarity, shared titles, and
visual resemblance were not used to merge identities. SVG originals and PNG
exports with different hashes remain separately inspectable.

The three excluded PNGs are `image.png`, `stream.png`, and
`twitch_despair.png`. Inspection showed editor, chat, or streaming application
screenshots rather than standalone artwork. The latter two also contain
third-party interface/content. Their original source hashes and exclusion
reasons are retained in the provenance manifest.

All files under the source `.unrenderable/` directory are excluded because they
already carry historical rendering quarantine. The other 26 excluded SVGs have
malformed XML, unbound namespace prefixes, or an incorrect SVG root namespace.
The snapshot does not silently repair or replace them.

## Featured artwork

`assets/featured-graphics.json` supplies 18 exact relative paths in presentation
order. Asset titles and categories remain filename-derived, following the
website's existing copy contract. These paths identify the selected originals;
they do not create new editorial titles.

1. `Sutured_Signal.svg`
2. `SCLDF_Seal_of_the_Fragmented_Ascent.svg`
3. `SCLDF_Seal_of_the_Indelible_Mark.svg`
4. `contract_promo.svg`
5. `fork_tales/fork_tales_radio_logo.svg`
6. `symmetry_oscillator.svg`
7. `SCLDF_Clojure_Saturated_Stability_Seal.svg`
8. `SCLDF_Seal_of_the_Agentic_Dialectic.svg`
9. `SCLDF_Sovereign_Refractive_Survival_Seal.svg`
10. `SCLDF_Synergy_Lamination_Seal.svg`
11. `recursive_identity_cult_logo.svg`
12. `SCLDF_Shattered_Equilibrium_Seal.svg`
13. `cyberpunk_contract_sync.svg`
14. `SCLDF_Seal_of_the_First_Word.svg`
15. `Seal_of_Divergent_Remorse.svg`
16. `SCLDF_Rhythmic_Anchor_Waveform.svg`
17. `recursive_identity/HyperSymmetryVoidSeal.svg`
18. `recursive_identity/Symmetry_Echo_Seal.svg`

The featured selection was made after inspecting contact sheets of the
non-quarantined collection. It favors legible geometric composition, a coherent
cyan/violet/gold palette, and a mixture of emblems, waveform art, and project
graphics. `Sutured_Signal.svg` supplies the wide lead image. The wider collection
also contains satirical certificates, diagrams, and narrative artifacts;
inclusion does not present them as client commissions.

## Provenance

The source repository is `riatzukiza/devel`; its observed snapshot HEAD was
`7b4565db7d2f65b3be1e1274a28fddb5926d1851`. Each manifest row records the original
relative `Graphics/...` path, SHA-256, byte size, format, available dimensions,
disposition, and latest available Git history for that specific source path.
Accepted rows also record the destination path under `assets/graphics/`.
The manifest contains no developer home-directory paths.

All 18 featured originals are recorded in source commit
`6ae61b0033cb6f761359595d2e9f13caef9841cc`, dated 2026-05-16, with Git author
`Err` and subject `Π: fork tax proxx/openplanner service snapshot (#45)`.
This establishes observed repository provenance; it does not independently
establish the original production method or a commercial commission.

One accepted local source file, `Graphics/symmetry_glaze.svg`, has no available
path history in the source checkout. Its `source_git` field is therefore `null`;
its observed local path, bytes, and SHA-256 remain recorded. No Git attribution
was fabricated for it.

## Verification boundary

Source XML/root namespaces and PNG decoding were checked, all copy hashes match,
all 18 featured paths resolve in the snapshot, and accepted hashes are unique.
The inspection contact sheets use a raster renderer that differs from a browser.
Five accepted SVGs contain `foreignObject`; they were also visually inspected
in Chromium, including their embedded text. No script elements or external `href` references were found in
the parseable SVG collection.

On 2026-10-09, a temporary browser fixture loaded all 442 built images eagerly:
**442 complete images with nonzero natural width, zero broken images, and zero
pending images**. The gallery viewer also opened, trapped keyboard focus, closed
with Escape, and restored focus to its originating card. The verification
fixture is removed by the final build and does not ship. Browser decoding is
not a claim that every internal SVG style, filter, or text layout is perfect;
original clipping and layout remain visible. The source files remain unchanged so
rendering behavior can be reviewed against the originals. A later publication
must verify the built static artifact and live URLs before claiming the public
site has changed.

Software/process documentation follows GPL-3.0-or-later. Existing creative-work
licensing and provenance remain applicable; this snapshot does not invent
individual authorship or replace source history.
