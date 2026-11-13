package com.example.util

/**
 * Bộ chuyển đổi Kana (Hiragana / Katakana) sang Romaji chuẩn Hepburn.
 */
object JapaneseRomajiConverter {

    private val digraphMap: Map<String, String> = mapOf(
        // Hiragana yoon & digraphs
        "きゃ" to "kya", "きゅ" to "kyu", "きょ" to "kyo",
        "しゃ" to "sha", "しゅ" to "shu", "しょ" to "sho",
        "ちゃ" to "cha", "ちゅ" to "chu", "ちょ" to "cho",
        "にゃ" to "nya", "にゅ" to "nyu", "にょ" to "nyo",
        "ひゃ" to "hya", "ひゅ" to "hyu", "ひょ" to "hyo",
        "みゃ" to "mya", "みゅ" to "myu", "みょ" to "myo",
        "りゃ" to "rya", "りゅ" to "ryu", "りょ" to "ryo",
        "ぎゃ" to "gya", "ぎゅ" to "gyu", "ぎょ" to "gyo",
        "じゃ" to "ja", "じゅ" to "ju", "じょ" to "jo",
        "ぢゃ" to "ja", "ぢゅ" to "ju", "ぢょ" to "jo",
        "びゃ" to "bya", "びゅ" to "byu", "びょ" to "byo",
        "ぴゃ" to "pya", "ぴゅ" to "pyu", "ぴょ" to "pyo",
        "ふぁ" to "fa", "ふぃ" to "fi", "ふぇ" to "fe", "ふぉ" to "fo",
        "てぃ" to "ti", "でぃ" to "di", "ちぇ" to "che", "しぇ" to "she", "じぇ" to "je",
        // Katakana yoon & foreign digraphs
        "キャ" to "kya", "キュ" to "kyu", "キョ" to "kyo",
        "シャ" to "sha", "シュ" to "shu", "ショ" to "sho",
        "チャ" to "cha", "チュ" to "chu", "チョ" to "cho",
        "ニャ" to "nya", "ニュ" to "nyu", "ニョ" to "nyo",
        "ヒャ" to "hya", "ヒュ" to "hyu", "ヒョ" to "hyo",
        "ミャ" to "mya", "ミュ" to "myu", "ミョ" to "myo",
        "リャ" to "rya", "リュ" to "ryu", "リョ" to "ryo",
        "ギャ" to "gya", "ギュ" to "gyu", "ギョ" to "gyo",
        "ジャ" to "ja", "ジュ" to "ju", "ジョ" to "jo",
        "ビャ" to "bya", "ビュ" to "byu", "ビョ" to "byo",
        "ピャ" to "pya", "ピュ" to "pyu", "ピョ" to "pyo",
        "ファ" to "fa", "フィ" to "fi", "フェ" to "fe", "フォ" to "fo",
        "ティ" to "ti", "ディ" to "di", "ウィ" to "wi", "ウェ" to "we", "ウォ" to "wo",
        "ヴァ" to "va", "ヴィ" to "vi", "ヴェ" to "ve", "ヴォ" to "vo",
        "チェ" to "che", "シェ" to "she", "ジェ" to "je",
        "トゥ" to "tu", "ドゥ" to "du"
    )

    private val singleMap: Map<Char, String> = mapOf(
        // Hiragana
        'あ' to "a", 'い' to "i", 'う' to "u", 'え' to "e", 'お' to "o",
        'か' to "ka", 'き' to "ki", 'く' to "ku", 'け' to "ke", 'こ' to "ko",
        'さ' to "sa", 'し' to "shi", 'す' to "su", 'せ' to "se", 'そ' to "so",
        'た' to "ta", 'ち' to "chi", 'つ' to "tsu", 'て' to "te", 'と' to "to",
        'な' to "na", 'に' to "ni", 'ぬ' to "nu", 'ね' to "ne", 'の' to "no",
        'は' to "ha", 'ひ' to "hi", 'ふ' to "fu", 'へ' to "he", 'ほ' to "ho",
        'ま' to "ma", 'み' to "mi", 'む' to "mu", 'め' to "me", 'も' to "mo",
        'や' to "ya", 'ゆ' to "yu", 'よ' to "yo",
        'ら' to "ra", 'り' to "ri", 'る' to "ru", 'れ' to "re", 'ろ' to "ro",
        'わ' to "wa", 'を' to "wo", 'ん' to "n",
        'が' to "ga", 'ぎ' to "gi", 'ぐ' to "gu", 'げ' to "ge", 'ご' to "go",
        'ざ' to "za", 'じ' to "ji", 'ず' to "zu", 'ぜ' to "ze", 'ぞ' to "zo",
        'だ' to "da", 'ぢ' to "ji", 'づ' to "zu", 'で' to "de", 'ど' to "do",
        'ば' to "ba", 'び' to "bi", 'ぶ' to "bu", 'べ' to "be", 'ぼ' to "bo",
        'ぱ' to "pa", 'ぴ' to "pi", 'ぷ' to "pu", 'ぺ' to "pe", 'ぽ' to "po",
        'ぁ' to "a", 'ぃ' to "i", 'ぅ' to "u", 'ぇ' to "e", 'ぉ' to "o",
        'ゃ' to "ya", 'ゅ' to "yu", 'ょ' to "yo",
        // Katakana
        'ア' to "a", 'イ' to "i", 'ウ' to "u", 'エ' to "e", 'オ' to "o",
        'カ' to "ka", 'キ' to "ki", 'ク' to "ku", 'ケ' to "ke", 'コ' to "ko",
        'サ' to "sa", 'シ' to "shi", 'ス' to "su", 'セ' to "se", 'ソ' to "so",
        'タ' to "ta", 'チ' to "chi", 'ツ' to "tsu", 'テ' to "te", 'ト' to "to",
        'ナ' to "na", 'ニ' to "ni", 'ヌ' to "nu", 'ネ' to "ne", 'ノ' to "no",
        'ハ' to "ha", 'ヒ' to "hi", 'フ' to "fu", 'ヘ' to "he", 'ホ' to "ho",
        'マ' to "ma", 'ミ' to "mi", 'ム' to "mu", 'メ' to "me", 'モ' to "mo",
        'ヤ' to "ya", 'ユ' to "yu", 'ヨ' to "yo",
        'ラ' to "ra", 'リ' to "ri", 'ル' to "ru", 'レ' to "re", 'ロ' to "ro",
        'ワ' to "wa", 'ヲ' to "wo", 'ン' to "n",
        'ガ' to "ga", 'ギ' to "gi", 'グ' to "gu", 'ゲ' to "ge", 'ゴ' to "go",
        'ザ' to "za", 'ジ' to "ji", 'ズ' to "zu", 'ゼ' to "ze", 'ゾ' to "zo",
        'ダ' to "da", 'ヂ' to "ji", 'ヅ' to "zu", 'デ' to "de", 'ド' to "do",
        'バ' to "ba", 'ビ' to "bi", 'ブ' to "bu", 'ベ' to "be", 'ボ' to "bo",
        'パ' to "pa", 'ピ' to "pi", 'プ' to "pu", 'ペ' to "pe", 'ポ' to "po",
        'ァ' to "a", 'ィ' to "i", 'ゥ' to "u", 'ェ' to "e", 'ォ' to "o",
        'ャ' to "ya", 'ュ' to "yu", 'ョ' to "yo", 'ヴ' to "vu",
        // Punctuation
        '〜' to "~", '・' to " ", '　' to " "
    )

    fun toRomaji(kana: String): String {
        if (kana.isBlank()) return ""
        val sb = StringBuilder()
        var i = 0
        val len = kana.length
        while (i < len) {
            val ch = kana[i]
            // Small tsu (gemination)
            if ((ch == 'っ' || ch == 'ッ') && i + 1 < len) {
                val nextSyllable = peekNextRomaji(kana, i + 1)
                if (nextSyllable.isNotEmpty()) {
                    val firstConsonant = nextSyllable[0]
                    if (firstConsonant in 'a'..'z' && firstConsonant !in "aeiou") {
                        // Hepburn rule: ch -> tch (e.g., まっち -> macchi or matchi; t before c -> tchi)
                        sb.append(if (nextSyllable.startsWith("ch")) 't' else firstConsonant)
                    }
                }
                i++
                continue
            }

            // Long vowel mark 'ー'
            if (ch == 'ー') {
                val lastVowel = sb.lastOrNull { it in "aeiou" }
                if (lastVowel != null) {
                    sb.append(lastVowel)
                }
                i++
                continue
            }

            // Two-char digraph check
            if (i + 1 < len) {
                val pair = kana.substring(i, i + 2)
                val mapped = digraphMap[pair]
                if (mapped != null) {
                    sb.append(mapped)
                    i += 2
                    continue
                }
            }

            // 'ん' / 'ン' before vowel or 'y' gets apostrophe for clarity (e.g., きんえん -> kin'en)
            if (ch == 'ん' || ch == 'ン') {
                val nextRomaji = if (i + 1 < len) peekNextRomaji(kana, i + 1) else ""
                sb.append('n')
                if (nextRomaji.isNotEmpty() && (nextRomaji[0] in "aeiouy")) {
                    sb.append('\'')
                }
                i++
                continue
            }

            sb.append(singleMap[ch] ?: ch)
            i++
        }
        return sb.toString().trim()
    }

    private fun peekNextRomaji(kana: String, index: Int): String {
        if (index >= kana.length) return ""
        if (index + 1 < kana.length) {
            val pair = kana.substring(index, index + 2)
            digraphMap[pair]?.let { return it }
        }
        return singleMap[kana[index]] ?: ""
    }
}
