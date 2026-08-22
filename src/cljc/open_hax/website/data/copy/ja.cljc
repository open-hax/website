(ns open-hax.website.data.copy.ja
  "Japanese message catalogue.

  Reviewed translation data, not machine output: this file is edited by a
  person and the build never translates. Key set and `{placeholder}` names are
  fixed by `open-hax.website.data.copy.en` and enforced by
  `open-hax.website.law.copy`.

  The two `{count}` sentences are the clearest argument for templating rather
  than concatenation: Japanese needs the counter `点` immediately after the
  numeral and places the clause differently from English, so no arrangement of
  a translated fragment plus a number produces this text.")

(def messages
  {:brand/name "OpenHax"
   :brand/promethean "Promethean"
   :link/github "GitHub"

   :meta/title "OpenHax — アート、音楽、プログラム"
   :meta/description "OpenHax のポートフォリオ。アート、音楽、ソフトウェアプロジェクトを紹介します。"
   :meta/noscript "このサイトのギャラリーと公開ドキュメントの表示には JavaScript が必要です。"

   :hero/tagline "アート、音楽、そしてプログラム"
   :hero/lede "創造的テクノロジーの限界を押し広げる、ビルダー・アーティスト・ミュージシャンの集団です。"
   :hero/cta-work "私たちの作品を見る"
   :hero/cta-github "GitHub で見る"

   :products/heading "プロダクト"
   :products/lede "次世代の創造的かつ知的なシステムを支えるインフラとツール。"
   :products/knoxx-description "エージェントのバックエンド、フロントエンド、ポリシーランタイム。エージェント基盤の中枢神経系です。"
   :products/proxx-description "モデルプロキシ、フェデレーション、資格情報リースの仲介。AI リクエストを賢くルーティングします。"
   :products/openplanner-description "メモリ、グラフ、プランニングの API。知識をつなぎ、複雑なワークフローを統括します。"

   :tag/agents "エージェント"
   :tag/policy "ポリシー"
   :tag/runtime "ランタイム"
   :tag/ai "AI"
   :tag/proxy "プロキシ"
   :tag/federation "フェデレーション"
   :tag/graph "グラフ"
   :tag/memory "メモリ"
   :tag/planning "プランニング"

   :graphics/heading "グラフィックス"
   :graphics/lede "{count} 点のビジュアル実験、ジェネレーティブアート、そして創造的な探求。"
   :graphics/close "閉じる"

   :music/heading "ミュージック"
   :music/lede "{count} 点の音の実験と楽曲。"

   :nav/products "プロダクト"
   :nav/graphics "グラフィックス"
   :nav/music "ミュージック"
   :nav/language "言語"

   :footer/copyright "© {year} OpenHax. 無断転載を禁じます。"

   :published/heading "公開ドキュメント"
   :published/untitled "無題のドキュメント"
   :published/not-found-title "ここには公開されたものがありません"
   :published/not-found-body "このアドレスには公開ドキュメントがありません。公開が取り下げられたか、リンクが誤っている可能性があります。"
   :published/back "サイトに戻る"

   :manifest/error-title "公開コンテンツのマニフェストが不正です"
   :manifest/error-body "サイトは自身のセクションを表示し続けます。マニフェストが修復されるまで、公開ドキュメントは利用できません。"})
