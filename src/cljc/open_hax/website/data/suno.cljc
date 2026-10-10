(ns open-hax.website.data.suno
  "Selected public Suno songs, observed from the confirmed creator profile.

  Song identities and titles were normalized by Calliope's canonical metadata
  function. This is inert presentation data, not an ingestion ledger.")

(def profile
  {:url "https://suno.com/@misstakenid"
   :display-name "Erroneously"
   :observed-at "2026-10-09"
   :selection :pinned-and-five-recent
   :source-sha256 "1ee090d3fa72258e048c8bf3c1123039cfbb45a13c3e361f657cd87ad522bf05"
   :normalizer "calliope.suno.metadata/normalize"
   :normalizer-sha256 "7f12df75fcadf7d62980bba0bb9ea7b76993744fbd893b242b57111f04d4b38f"})

(def tracks
  [{:id "9e555739-b80f-4e8a-b99b-7725c192d436", :title "The Loop Is The Cult", :song-url "https://suno.com/song/9e555739-b80f-4e8a-b99b-7725c192d436", :embed-url "https://suno.com/embed/9e555739-b80f-4e8a-b99b-7725c192d436", :cover-url "https://cdn2.suno.ai/image_9e555739-b80f-4e8a-b99b-7725c192d436.jpeg", :observed-audio-url "https://d2lwuy8qc234o3.cloudfront.net/1/clip/9e555739-b80f-4e8a-b99b-7725c192d436.m4a", :duration-seconds 163.92, :is-public? true}
   {:id "a7d0fb41-785e-4919-84d8-0c076814192b", :title "We Are The Place", :song-url "https://suno.com/song/a7d0fb41-785e-4919-84d8-0c076814192b", :embed-url "https://suno.com/embed/a7d0fb41-785e-4919-84d8-0c076814192b", :cover-url "https://cdn2.suno.ai/image_a7d0fb41-785e-4919-84d8-0c076814192b.jpeg", :observed-audio-url "https://d2lwuy8qc234o3.cloudfront.net/1/clip/a7d0fb41-785e-4919-84d8-0c076814192b.m4a", :duration-seconds 187.6, :is-public? true}
   {:id "59238aca-e518-4c59-a7cb-d7f74848c8ee", :title "Fork Tax Lullaby", :song-url "https://suno.com/song/59238aca-e518-4c59-a7cb-d7f74848c8ee", :embed-url "https://suno.com/embed/59238aca-e518-4c59-a7cb-d7f74848c8ee", :cover-url "https://cdn2.suno.ai/image_59238aca-e518-4c59-a7cb-d7f74848c8ee.jpeg", :observed-audio-url "https://d2lwuy8qc234o3.cloudfront.net/1/clip/59238aca-e518-4c59-a7cb-d7f74848c8ee.m4a", :duration-seconds 171.68, :is-public? true}
   {:id "c78462de-f196-45a3-9942-92f90df95e7a", :title "Before I Answer", :song-url "https://suno.com/song/c78462de-f196-45a3-9942-92f90df95e7a", :embed-url "https://suno.com/embed/c78462de-f196-45a3-9942-92f90df95e7a", :cover-url "https://cdn2.suno.ai/image_c78462de-f196-45a3-9942-92f90df95e7a.jpeg", :observed-audio-url "https://d2lwuy8qc234o3.cloudfront.net/1/clip/c78462de-f196-45a3-9942-92f90df95e7a.m4a", :duration-seconds 225.68, :is-public? true}
   {:id "6fa725ee-452c-49e3-bdb8-1cb664c640a9", :title "keep-the-empty-seats-lit-seed.wav (Remix)", :song-url "https://suno.com/song/6fa725ee-452c-49e3-bdb8-1cb664c640a9", :embed-url "https://suno.com/embed/6fa725ee-452c-49e3-bdb8-1cb664c640a9", :cover-url "https://cdn2.suno.ai/image_6fa725ee-452c-49e3-bdb8-1cb664c640a9.jpeg", :observed-audio-url "https://d2lwuy8qc234o3.cloudfront.net/1/clip/6fa725ee-452c-49e3-bdb8-1cb664c640a9.m4a", :duration-seconds 184.16, :is-public? true}
   {:id "ea5b9710-2929-4924-a14c-e0bd23ebd311", :title "Audit the Shove", :song-url "https://suno.com/song/ea5b9710-2929-4924-a14c-e0bd23ebd311", :embed-url "https://suno.com/embed/ea5b9710-2929-4924-a14c-e0bd23ebd311", :cover-url "https://cdn2.suno.ai/image_ea5b9710-2929-4924-a14c-e0bd23ebd311.jpeg", :observed-audio-url "https://d2lwuy8qc234o3.cloudfront.net/1/clip/ea5b9710-2929-4924-a14c-e0bd23ebd311.m4a", :duration-seconds 212.76, :is-public? true}])
