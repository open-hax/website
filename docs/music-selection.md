# Published Suno selection

The OpenHax personal hub features six songs from the user-confirmed profile
[Erroneously (@misstakenid)](https://suno.com/@misstakenid). This is a focused
selection, not a claim to catalogue every song the artist has published.

## Publication evidence

The profile was retrieved successfully on **2026-10-09**. Its public
server-rendered response included a `user_songs` feed and exact clip queries
for twenty songs. All twenty inspected clip objects had `is_public: true` and
the creator handle `misstakenid`. The selection uses the profile's pinned song
and the five recent songs linked visibly on its initial page:

| Song | Exact Suno clip identity |
| --- | --- |
| The Loop Is The Cult | [9e555739-b80f-4e8a-b99b-7725c192d436](https://suno.com/song/9e555739-b80f-4e8a-b99b-7725c192d436) |
| We Are The Place | [a7d0fb41-785e-4919-84d8-0c076814192b](https://suno.com/song/a7d0fb41-785e-4919-84d8-0c076814192b) |
| Fork Tax Lullaby | [59238aca-e518-4c59-a7cb-d7f74848c8ee](https://suno.com/song/59238aca-e518-4c59-a7cb-d7f74848c8ee) |
| Before I Answer | [c78462de-f196-45a3-9942-92f90df95e7a](https://suno.com/song/c78462de-f196-45a3-9942-92f90df95e7a) |
| keep-the-empty-seats-lit-seed.wav (Remix) | [6fa725ee-452c-49e3-bdb8-1cb664c640a9](https://suno.com/song/6fa725ee-452c-49e3-bdb8-1cb664c640a9) |
| Audit the Shove | [ea5b9710-2929-4924-a14c-e0bd23ebd311](https://suno.com/song/ea5b9710-2929-4924-a14c-e0bd23ebd311) |

Downloaded files, likes, collection membership, and a working song URL were not
used as publication proof. Suno's [sharing documentation](https://help.suno.com/en/articles/2565761)
distinguishes Public songs from Private/Link Only songs, which can still have
working direct links.

## Canonical metadata boundary

Calliope owns Suno metadata intake. The documented workflow is
`clojure -M:suno-meta import FILE_OR_DIRECTORY`, followed by
`clojure -M:suno-meta project`; its design is
`calliope/docs/designs/suno-listening-companion.md` in Foresight. This website
does not introduce an importer, ingestion ledger, or projection authority.

The selected exact public clip objects were inspected ephemerally and consumed
through the existing portable `calliope.suno.metadata/normalize` function via
NBB. That function validated UUIDs, titles, duration, and metadata, removed
account/credential fields, and derived canonical `https://suno.com/song/UUID`
URLs. No Calliope ledger or projection was modified. Clip titles are retained
exactly; equal titles never merge identities.

`src/cljc/open_hax/website/data/suno.cljc` holds only inert site presentation
data: selected identities, titles, song/embed URLs, cover/audio references,
duration, and explicit public flags. Its profile record preserves the capture
date, selection rule, source response SHA-256, and canonical normalizer source
SHA-256. Full provider responses remain temporary local inspection material.
No credentials, account identifiers, lyrics, or downloaded media library are
committed in that data.

## Player and delivery behavior

The official [Suno embed for We Are The Place](https://suno.com/embed/a7d0fb41-785e-4919-84d8-0c076814192b)
returned HTTP 200 HTML with that exact song title and an embedded player on
2026-10-09. The song page advertises Suno's own oEmbed endpoint. Cards use
`https://suno.com/embed/UUID` rather than freezing audio delivery URLs.

Initially the site shows each song's cover, title, and direct song link. A
listener clicks **Listen here** to load one official player; selecting another
card removes the previous embed. The site does not request autoplay. The full
artist profile remains linked for further listening.

The observed profile currently sets `audio_url` to a forbidden endpoint and
supplies playable progressive references separately in `media_urls`. Those
references are preserved as observation data, but the website does not play
them directly: CDN policy and delivery URLs may change independently of clip
identity. Cover images remain public provider references. No remote songs or
covers were bulk-downloaded or staged.

The existing native player remains available for intentionally staged local
tracks and uses `preload="none"` with no autoplay. An empty selection and empty
local collection render no music section. On 2026-10-09, Chromium loaded all six cover images successfully and initially
rendered no iframe. Selecting We Are The Place loaded the exact official embed;
clicking its play control produced an advancing playhead with duration 187.6
seconds, readyState 4, paused false, and no audio error. Each of the other five selected players was then tested: all six reached an
advancing playhead, readyState 4, paused false, and no audio error. Switching
tracks kept exactly one iframe present. Final mobile dimensions and the live
deployment remain separate verification steps.

Software and process documentation: GPL-3.0-or-later. Creative work retains its
existing ownership and licensing.
