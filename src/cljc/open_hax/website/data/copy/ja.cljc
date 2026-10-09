(ns open-hax.website.data.copy.ja
  "Japanese message catalogue.

  Translation data requires human review before publication. See
  `docs/site-copy-review.md` for the current approval status. The build never
  translates. Key set and `{placeholder}` names are
  fixed by `open-hax.website.data.copy.en` and enforced by
  `open-hax.website.law.copy`.

  Count templates keep Japanese counters specific to each message: artwork
  uses `点`, while songs use `曲`. The counter follows its numeral, and the
  clause order differs from English, so translated fragments and a number
  cannot replace the complete template.")

(def messages
  {:brand/name "OpenHax"
   :brand/promethean "Promethean"
   :link/github "GitHub"

   :meta/title "OpenHax — アート、音楽、プログラム"
   :meta/description "OpenHaxの個人ポートフォリオ。ウェブサイト、オープンソースソフトウェア、生成アート、音楽を紹介します。"
   :meta/noscript "このサイトのギャラリーと公開ドキュメントの表示には JavaScript が必要です。"

   :hero/tagline "ウェブサイト、アート、音楽、役立つソフトウェア"
   :hero/lede "開発者であり、アーティストです。OpenHaxでは、オリジナルのビジュアル作品、創作実験、開発したソフトウェアを紹介しています。"
   :hero/cta-work "作品を見る"
   :hero/cta-github "GitHubで連絡する"

   :products/heading "ソフトウェアプロジェクト"
   :products/lede "開発や貢献をしているオープンソースのツールです。各プロジェクトのコード、アイデア、参加者をご覧ください。"
   :products/knoxx-description "会話、ツール、ポリシーを備えたエージェントの実行環境。OpenHaxの独立したプロジェクトです。"
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

   :graphics/heading "アートとグラフィックス"
   :graphics/lede "幾何学の習作、生成パターン、デジタルイラストなど、{count}点のビジュアル作品。"
   :hero/client-lede "ビジネスのウェブサイトをお考えですか？あなたの仕事を伝える、わかりやすく個性的なサイトを作ります。プロジェクトについてお気軽にご相談ください。"
   :graphics/featured "作品セレクション"
   :graphics/collection "コレクションを見る"
   :graphics/search "作品を検索"
   :graphics/all "すべてのコレクション"
   :graphics/more "さらに作品を見る"
   :graphics/results "{count}点中{shown}点を表示"
   :graphics/empty "検索に一致する作品がありません。"
   :graphics/open "{title}を見る"

   :graphics/close "閉じる"

   :music/heading "ミュージック"
   :music/lede "Sunoで公開された楽曲から{count}曲を紹介。"
   :music/listen "ここで聴く"
   :music/play "{title}を聴く"
   :music/song-link "Sunoで開く"
   :music/player-label "{title}のSunoプレーヤー"
   :music/profile "Sunoで{name}のほかの音楽を聴く"
   :music/local-heading "ローカルコレクションから"
   :music/local-lede "さらに{count}曲。"

   :nav/products "ソフトウェア"
   :nav/graphics "アート"
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
