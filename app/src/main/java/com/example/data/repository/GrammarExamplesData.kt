package com.example.data.repository

import com.example.data.model.GrammarExample

/**
 * Kho câu ví dụ chuẩn JLPT N3 cho toàn bộ 134 điểm ngữ pháp (Bài 1 - Bài 27).
 * Đảm bảo mỗi cấu trúc ngữ pháp đều có câu tiếng Nhật hoàn chỉnh có thể đục lỗ (blank out)
 * mà không bao giờ làm lộ đáp án trong câu hỏi.
 */
object GrammarExamplesData {

    data class GrammarDrillTemplate(
        val jp: String,
        val vn: String,
        val targetSegment: String
    )

    private val templatesByExactTitle: Map<String, GrammarDrillTemplate> = mapOf(
        // Bài 1
        "〜つまり" to GrammarDrillTemplate("父の兄、つまり私のおじは医者をしています。", "Anh trai của bố tôi, tức là bác tôi, đang làm bác sĩ.", "つまり"),
        "〜。ということは〜。" to GrammarDrillTemplate("ご意見がないということは、賛成ということですね。", "Không có ý kiến gì, vậy tức là mọi người đều tán thành đúng không.", "ということは"),
        "〜。というのは〜。" to GrammarDrillTemplate("今度の旅行には行けません。というのは、急な仕事が入ったからです。", "Chuyến du lịch lần này tôi không đi được. Lý do là vì có công việc đột xuất.", "というのは"),
        "〜というと" to GrammarDrillTemplate("日本の春というと、やはり桜を思い出します。", "Nhắc đến mùa xuân của Nhật Bản, quả nhiên người ta nhớ ngay đến hoa anh đào.", "というと"),
        "〜ということだ" to GrammarDrillTemplate("天気予報によると、明日は一日中雨が降るということだ。", "Theo dự báo thời tiết, nghe nói ngày mai trời sẽ mưa cả ngày.", "ということだ"),

        // Bài 2
        "〜というの〜" to GrammarDrillTemplate("「一期一会」というのは、一生に一度の出会いという意味です。", "Cái gọi là 'Nhất kỳ nhất hội' mang ý nghĩa là cuộc gặp gỡ chỉ có một lần trong đời.", "というのは"),
        "〜というのは" to GrammarDrillTemplate("「ボランティア」というのは、自分から進んで社会のために働くことです。", "'Tình nguyện' có nghĩa là tự mình chủ động làm việc vì xã hội.", "というのは"),
        "〜というより" to GrammarDrillTemplate("彼は先生というより、優しいお兄さんのような人だ。", "Anh ấy đúng hơn là giống một người anh trai hiền lành hơn là thầy giáo.", "というより"),
        "〜。それとも〜。" to GrammarDrillTemplate("コーヒーにしますか。それとも、紅茶にしますか。", "Bạn dùng cà phê nhé? Hay là dùng trà đen?", "それとも"),
        "〜。それなら〜。" to GrammarDrillTemplate("明日は雨が降るそうです。それなら、ピクニックは中止しましょう。", "Nghe nói ngày mai trời mưa. Nếu vậy thì chúng ta hãy hoãn buổi dã ngoại lại.", "それなら"),

        // Bài 3
        "〜それと〜" to GrammarDrillTemplate("ノートとペン、それと消しゴムも買っておいてください。", "Hãy mua giúp tôi vở và bút, thêm nữa cả cục tẩy nữa nhé.", "それと"),
        "だけど" to GrammarDrillTemplate("毎日練習している。だけど、なかなか上手にならない。", "Tôi luyện tập mỗi ngày. Nhưng mà vẫn mãi chưa giỏi lên được.", "だけど"),
        "〜ところが" to GrammarDrillTemplate("急いで駅へ向かった。ところが、電車はもう出た後だった。", "Tôi vội vã chạy ra ga. Thế nhưng tàu đã chạy mất rồi.", "ところが"),
        "〜その上〜" to GrammarDrillTemplate("この部屋は広くて日当たりがいい。その上、家賃も安い。", "Căn phòng này rộng và đón nắng tốt. Hơn nữa, tiền thuê nhà cũng rẻ.", "その上"),
        "ところで" to GrammarDrillTemplate("仕事の話は終わりましたね。ところで、週末の予定はどうですか。", "Chuyện công việc xong rồi nhé. Nhân tiện, kế hoạch cuối tuần thế nào?", "ところで"),

        // Bài 4
        "〜。さて〜。" to GrammarDrillTemplate("準備ができました。さて、これから会議を始めましょう。", "Đã chuẩn bị xong rồi. Bây giờ thì chúng ta hãy bắt đầu cuộc họp nào.", "さて"),
        "〜。すると〜。" to GrammarDrillTemplate("ドアを開けた。すると、かわいい猫が座っていた。", "Tôi mở cửa ra. Ngay sau đó thấy một chú mèo dễ thương đang ngồi.", "すると"),
        "〜。そういえば〜。" to GrammarDrillTemplate("昨日駅で田中さんに会ったよ。そういえば、来月結婚するそうだね。", "Hôm qua tôi gặp anh Tanaka ở ga đấy. Nhắc mới nhớ, nghe nói tháng sau anh ấy kết hôn nhỉ.", "そういえば"),
        "〜。それが〜。" to GrammarDrillTemplate("試験は簡単だと思っていた。それが、思ったよりずっと難しかった。", "Tôi cứ tưởng bài thi dễ. Vậy mà khó hơn tôi nghĩ rất nhiều.", "それが"),
        "もし〜ても" to GrammarDrillTemplate("もし失敗しても、あきらめないで何度でも挑戦します。", "Cho dù có thất bại đi nữa, tôi cũng không bỏ cuộc mà sẽ thử thách lại nhiều lần.", "ても"),

        // Bài 5
        "もしも〜なら" to GrammarDrillTemplate("もしもタイムマシンがあるなら、過去に戻ってみたいです。", "Nếu như có cỗ máy thời gian, tôi muốn thử quay về quá khứ.", "なら"),
        "〜ば〜のに" to GrammarDrillTemplate("もっと早く家を出れば、電車に間に合ったのに。", "Giá mà tôi ra khỏi nhà sớm hơn thì đã kịp chuyến tàu rồi.", "のに"),
        "〜ば〜ほど" to GrammarDrillTemplate("日本語は勉強すればするほど、面白くなってきます。", "Tiếng Nhật càng học thì càng thấy trở nên thú vị.", "ほど"),
        "〜たとえ〜ても" to GrammarDrillTemplate("たとえ雨が降っても、明日の運動会は行われます。", "Cho dù trời có mưa, hội thao ngày mai vẫn được tổ chức.", "ても"),
        "〜ないと" to GrammarDrillTemplate("もうすぐ締め切りだから、急いでレポートを出さないと。", "Sắp đến hạn chót rồi nên nếu không nộp báo cáo gấp thì không xong.", "ないと"),

        // Bài 6
        "〜としたら" to GrammarDrillTemplate("一億円当たったとしたら、まず世界一周旅行に行きたい。", "Giả sử trúng một trăm triệu yên, trước tiên tôi muốn đi du lịch vòng quanh thế giới.", "としたら"),
        "もし〜たなら" to GrammarDrillTemplate("もし昨日よく寝たなら、今日はもっと元気だっただろう。", "Nếu hôm qua ngủ ngon giấc thì chắc hôm nay đã khỏe hơn rồi.", "たなら"),
        "〜ばよかった" to GrammarDrillTemplate("傘を持ってくればよかったと、雨に濡れて後悔した。", "Bị ướt mưa nên tôi hối hận giá mà đã mang theo ô.", "ばよかった"),
        "〜ても" to GrammarDrillTemplate("いくら説明しても、彼は納得してくれなかった。", "Dù giải thích bao nhiêu lần đi nữa, anh ấy vẫn không chịu hiểu.", "ても"),
        "〜といっても" to GrammarDrillTemplate("料理ができるといっても、簡単な卵焼きくらいしか作れません。", "Dù nói là biết nấu ăn, nhưng tôi chỉ làm được món trứng cuộn đơn giản thôi.", "といっても"),

        // Bài 7
        "〜つもりでした" to GrammarDrillTemplate("朝6時に起きるつもりでしたが、寝坊してしまいました。", "Tôi định dậy lúc 6 giờ sáng, nhưng lại ngủ quên mất.", "つもりでした"),
        "〜ようとしない" to GrammarDrillTemplate("弟は何度注意しても、部屋を片付けようとしない。", "Em trai tôi dù nhắc nhở bao nhiêu lần cũng không chịu dọn phòng.", "ようとしない"),
        "〜てごらん" to GrammarDrillTemplate("このケーキ、とても美味しいから一度食べてごらん。", "Chiếc bánh này ngon lắm, thử ăn một lần xem.", "てごらん"),
        "〜てくれと" to GrammarDrillTemplate("母に買い物に行ってくれと頼まれた。", "Tôi được mẹ nhờ đi mua đồ giúp.", "てくれと"),
        "〜てほしい" to GrammarDrillTemplate("大切な話があるので、ぜひ最後まで聞いてほしい。", "Vì có chuyện quan trọng nên tôi rất muốn bạn lắng nghe đến cuối.", "てほしい"),

        // Bài 8
        "〜ことだ" to GrammarDrillTemplate("日本語が上手になりたければ、毎日話すことだ。", "Nếu muốn giỏi tiếng Nhật thì nên luyện nói mỗi ngày.", "ことだ"),
        "〜ようにしましょう" to GrammarDrillTemplate("健康のために、毎日30分は歩くようにしましょう。", "Vì sức khỏe, hãy cố gắng đi bộ 30 phút mỗi ngày.", "ようにしましょう"),
        "〜といいなあ" to GrammarDrillTemplate("明日の遠足、晴れるといいなあ。", "Mong là buổi dã ngoại ngày mai trời sẽ nắng.", "といいなあ"),
        "〜ようとした" to GrammarDrillTemplate("家を出ようとした時、急に電話が鳴った。", "Đúng lúc tôi định bước ra khỏi nhà thì điện thoại đột nhiên reo.", "ようとした"),
        "〜つもりだ" to GrammarDrillTemplate("卒業したら、日本の会社で働くつもりだ。", "Sau khi tốt nghiệp, tôi dự định sẽ làm việc ở công ty Nhật.", "つもりだ"),

        // Bài 9
        "〜つもりで" to GrammarDrillTemplate("最後のチャンスだというつもりで、全力で試験に臨んだ。", "Với quyết tâm đây là cơ hội cuối cùng, tôi dốc toàn lực bước vào kỳ thi.", "つもりで"),
        "〜くらいは〜" to GrammarDrillTemplate("いくら忙しくても、朝ごはんくらいは食べたほうがいい。", "Dù bận đến đâu thì ít nhất bữa sáng cũng nên ăn.", "くらいは"),
        "〜こそ" to GrammarDrillTemplate("今年こそ、JLPT N3に必ず合格してみせます。", "Chính năm nay, tôi nhất định sẽ thi đỗ JLPT N3 cho xem.", "こそ"),
        "〜さ" to GrammarDrillTemplate("山の頂上から見る景色の美しさは言葉にできない。", "Vẻ đẹp của phong cảnh nhìn từ đỉnh núi không thể diễn tả bằng lời.", "さ"),
        "〜さえ" to GrammarDrillTemplate("忙しくて、昼ごはんを食べる時間さえありません。", "Bận rộn đến nỗi ngay cả thời gian ăn trưa cũng không có.", "さえ"),

        // Bài 10
        "どんなに〜ても" to GrammarDrillTemplate("どんなに難しくても、最後まであきらめずにやり遂げます。", "Dù có khó đến thế nào, tôi cũng không bỏ cuộc và làm đến cùng.", "ても"),
        "〜ほど〜" to GrammarDrillTemplate("涙が出るほど笑って、お腹が痛くなった。", "Cười đến mức chảy cả nước mắt, đau cả bụng.", "ほど"),
        "〜どんなに〜ことか" to GrammarDrillTemplate("合格の知らせを聞いた時、どんなに嬉しかったことか。", "Khi nghe tin đỗ kỳ thi, tôi đã vui sướng biết bao.", "ことか"),
        "まるで〜よう" to GrammarDrillTemplate("この町は静かで、まるで絵の中の世界のようだ。", "Thị trấn này yên tĩnh giống hệt như thế giới trong tranh vẽ vậy.", "ようだ"),
        "〜おかげで" to GrammarDrillTemplate("先生のご指導のおかげで、JLPT N3に合格できました。", "Nhờ sự chỉ bảo tận tình của thầy giáo mà em đã thi đỗ JLPT N3.", "おかげで"),

        // Bài 11
        "〜せいで" to GrammarDrillTemplate("バスが遅れたせいで、約束の時間に間に合いませんでした。", "Tại vì xe buýt đến muộn nên tôi đã không kịp giờ hẹn.", "せいで"),
        "〜。だって〜。" to GrammarDrillTemplate("今日は外出したくない。だって、外はとても寒いんだもん。", "Hôm nay tôi không muốn ra ngoài. Tại vì ngoài trời lạnh lắm mà.", "だって"),
        "〜そのために" to GrammarDrillTemplate("台風が近づいている。そのために、多くの電車が止まった。", "Bão đang đến gần. Vì vậy nhiều chuyến tàu đã ngừng chạy.", "そのために"),
        "〜その結果" to GrammarDrillTemplate("毎日コツコツ練習した。その結果、スピーチ大会で優勝した。", "Tôi luyện tập chăm chỉ mỗi ngày. Kết quả là đã giành giải nhất cuộc thi hùng biện.", "その結果"),
        "ですから〜" to GrammarDrillTemplate("明日は朝早く出発します。ですから、今夜は早く寝ます。", "Ngày mai tôi xuất phát sớm. Vì thế tối nay tôi sẽ đi ngủ sớm.", "ですから"),

        // Bài 12
        "〜なぜなら" to GrammarDrillTemplate("この店が人気な理由は味だ。なぜなら、新鮮な魚しか使わないからだ。", "Lý do quán này nổi tiếng là hương vị. Bởi vì họ chỉ dùng cá tươi.", "なぜなら"),
        "〜ついでに" to GrammarDrillTemplate("郵便局へ行くついでに、この手紙を出してください。", "Tiện thể lúc đi bưu điện, hãy gửi giúp tôi bức thư này.", "ついでに"),
        "〜てからでないと" to GrammarDrillTemplate("両親と相談してからでないと、返事ができません。", "Nếu không bàn bạc với bố mẹ trước thì tôi không thể trả lời được.", "てからでないと"),
        "〜てはじめて" to GrammarDrillTemplate("日本に来てはじめて、一人で料理を作りました。", "Chỉ khi sang Nhật tôi mới lần đầu tiên tự nấu ăn.", "てはじめて"),
        "〜ところだった" to GrammarDrillTemplate("階段で滑って、もう少しで転ぶところだった。", "Trượt chân ở cầu thang, chút nữa thì ngã rồi.", "ところだった"),

        // Bài 13
        "〜ところ" to GrammarDrillTemplate("ちょうどこれから昼ごはんを食べるところです。", "Đúng lúc bây giờ tôi chuẩn bị ăn trưa.", "ところ"),
        "〜たて" to GrammarDrillTemplate("このパンは焼きたてなので、とてもいい香りがします。", "Bánh mì này vừa mới nướng xong nên mùi thơm lắm.", "たて"),
        "〜最中に" to GrammarDrillTemplate("大切な会議の最中に、携帯電話が鳴ってしまった。", "Đúng giữa lúc họp quan trọng thì điện thoại lại reo lên.", "最中に"),
        "〜たびに" to GrammarDrillTemplate("この曲を聴くたびに、高校時代のことを思い出します。", "Mỗi khi nghe bài hát này, tôi lại nhớ về thời cấp ba.", "たびに"),
        "〜たとたん" to GrammarDrillTemplate("電気を消したとたん、部屋が真っ暗になった。", "Ngay khi tắt điện, căn phòng trở nên tối om.", "たとたん"),

        // Bài 14
        "〜たところ" to GrammarDrillTemplate("病院で検査してもらったところ、どこも悪くなかった。", "Sau khi đi khám ở bệnh viện thì thấy không có chỗ nào bị bệnh cả.", "たところ"),
        "〜うちに" to GrammarDrillTemplate("雨が降らないうちに、急いで家に帰りましょう。", "Tranh thủ lúc trời chưa mưa, chúng ta hãy nhanh về nhà thôi.", "うちに"),
        "すこしも〜ない" to GrammarDrillTemplate("薬を飲んだのに、熱がすこしも下がらない。", "Dù đã uống thuốc nhưng cơn sốt không giảm chút nào.", "すこしも"),
        "〜せずに" to GrammarDrillTemplate("今朝は朝ごはんを食べずに、急いで学校へ行った。", "Sáng nay tôi không ăn sáng mà vội vã đi học luôn.", "せずに"),
        "まったく〜ない" to GrammarDrillTemplate("彼の言っている意味がまったく分からない。", "Tôi hoàn toàn không hiểu ý anh ấy đang nói gì.", "まったく"),

        // Bài 15
        "〜わけがない" to GrammarDrillTemplate("あんなに真面目な田中さんが嘘をつくわけがない。", "Người nghiêm túc như anh Tanaka không đời nào lại nói dối.", "わけがない"),
        "〜わけではない" to GrammarDrillTemplate("甘いものが嫌いなわけではないが、今は食べたくない。", "Không hẳn là tôi ghét đồ ngọt, nhưng bây giờ không muốn ăn.", "わけではない"),
        "〜わけにはいかない" to GrammarDrillTemplate("明日は大事な試験があるので、今夜は寝るわけにはいかない。", "Ngày mai có kỳ thi quan trọng nên đêm nay không thể ngủ sớm được.", "わけにはいかない"),
        "〜ないことはない" to GrammarDrillTemplate("少し辛いですが、食べられないことはないです。", "Hơi cay một chút nhưng không phải là không ăn được.", "ないことはない"),
        "〜めったに〜ない" to GrammarDrillTemplate("この地域では冬でも雪がめったに降らない。", "Ở khu vực này ngay cả mùa đông tuyết cũng hiếm khi rơi.", "めったに"),

        // Bài 16
        "〜ないこともない" to GrammarDrillTemplate("値段は高いけれど、頑張れば買えないこともない。", "Giá hơi đắt nhưng nếu cố gắng thì cũng không phải là không mua được.", "ないこともない"),
        "〜めったにない" to GrammarDrillTemplate("こんな素晴らしい機会は一生にめったにない。", "Cơ hội tuyệt vời như thế này cả đời hiếm có.", "めったにない"),
        "〜そうもない" to GrammarDrillTemplate("この分では、雨はしばらくやみそうもない。", "Với tình hình này thì mưa khó mà tạnh sớm được.", "そうもない"),
        "必ずしも〜とは限らない" to GrammarDrillTemplate("高いものが必ずしも品質が良いとは限らない。", "Đồ đắt tiền chưa chắc chất lượng đã tốt.", "とは限らない"),
        "決して〜ない" to GrammarDrillTemplate("この秘密は誰にも決して話してはいけない。", "Tuyệt đối không được kể bí mật này cho bất kỳ ai.", "決して"),

        // Bài 17
        "〜ずにすんだ" to GrammarDrillTemplate("友達が傘を貸してくれたので、雨に濡れずにすんだ。", "Nhờ bạn cho mượn ô nên tôi không cần dầm mưa mà vẫn về được.", "ずにすんだ"),
        "〜そうにない" to GrammarDrillTemplate("仕事が多すぎて、今日中に終わりそうにない。", "Công việc nhiều quá nên trông không thể xong trong hôm nay nổi.", "そうにない"),
        "〜ないで" to GrammarDrillTemplate("辞書を使わないで、この新聞の記事を読んでみてください。", "Hãy thử đọc bài báo này mà không dùng từ điển.", "ないで"),
        "〜ことはない" to GrammarDrillTemplate("まだ時間があるから、そんなに急ぐことはないよ。", "Vẫn còn thời gian mà, không cần phải vội như thế đâu.", "ことはない"),
        "〜にかわって" to GrammarDrillTemplate("入院中の部長にかわって、私が会議に出席します。", "Thay mặt trưởng phòng đang nằm viện, tôi sẽ tham dự cuộc họp.", "にかわって"),

        // Bài 18
        "〜にしては" to GrammarDrillTemplate("彼は日本に来て半年にしては、日本語がとても上手だ。", "So với việc mới sang Nhật nửa năm thì tiếng Nhật của anh ấy rất giỏi.", "にしては"),
        "〜にしても" to GrammarDrillTemplate("冗談にしても、そんなことを言うべきではない。", "Dù là nói đùa đi nữa cũng không nên nói những điều như thế.", "にしても"),
        "〜について" to GrammarDrillTemplate("日本の文化について、レポートを書いています。", "Tôi đang viết báo cáo về văn hóa Nhật Bản.", "について"),
        "〜にとって" to GrammarDrillTemplate("留学生にとって、漢字の勉強は一番大変だ。", "Đối với du học sinh, việc học Kanji là vất vả nhất.", "にとって"),
        "〜によって" to GrammarDrillTemplate("国によって、生活習慣や考え方が違います。", "Tùy theo từng quốc gia mà thói quen sinh hoạt và cách suy nghĩ khác nhau.", "によって"),

        // Bài 19
        "〜によれば" to GrammarDrillTemplate("ニュースによれば、来月から電気代が上がるそうだ。", "Theo như tin tức, nghe nói từ tháng sau tiền điện sẽ tăng.", "によれば"),
        "〜に対して" to GrammarDrillTemplate("目上の人に対しては、丁寧な言葉を使わなければならない。", "Đối với người bề trên thì phải sử dụng lời nói lịch sự.", "に対して"),
        "〜に比べて" to GrammarDrillTemplate("兄に比べて、弟はとてもおとなしい性格だ。", "So với anh trai, em trai có tính cách rất điềm đạm.", "に比べて"),
        "〜に関して" to GrammarDrillTemplate("この計画に関して、何か質問はありませんか。", "Liên quan đến kế hoạch này, có câu hỏi nào không?", "に関して"),
        "〜など" to GrammarDrillTemplate("休みの日は映画を見たり、本を読んだりなどして過ごします。", "Ngày nghỉ tôi thường xem phim, đọc sách chẳng hạn.", "など"),

        // Bài 20
        "〜ばかり" to GrammarDrillTemplate("弟は勉強もしないで、毎日ゲームばかりしている。", "Em trai tôi chẳng học hành gì, ngày nào cũng chỉ toàn chơi game.", "ばかり"),
        "〜ばかりか〜も" to GrammarDrillTemplate("彼は英語ばかりか、中国語もペラペラに話せる。", "Anh ấy không chỉ tiếng Anh mà tiếng Trung cũng nói lưu loát.", "ばかりか"),
        "〜はもちろん〜も" to GrammarDrillTemplate("田中さんは漢字はもちろん、敬語も完璧に使いこなす。", "Anh Tanaka không chỉ chữ Hán mà kính ngữ cũng dùng thành thạo.", "はもちろん"),
        "〜とか" to GrammarDrillTemplate("京都とか奈良とか、古いお寺が多い街が好きです。", "Tôi thích những thành phố có nhiều chùa cổ như Kyoto hay Nara.", "とか"),
        "〜において" to GrammarDrillTemplate("次のオリンピックは東京において開催される予定だ。", "Thế vận hội tiếp theo dự kiến được tổ chức tại Tokyo.", "において"),

        // Bài 21
        "〜から〜にかけて" to GrammarDrillTemplate("今夜から明日の朝にかけて、強い雨が降るでしょう。", "Từ đêm nay đến khoảng sáng mai trời sẽ có mưa lớn.", "にかけて"),
        "〜として" to GrammarDrillTemplate("彼はボランティアとして、被災地で活動している。", "Anh ấy đang hoạt động tại vùng thiên tai với tư cách tình nguyện viên.", "として"),
        "〜かわりに" to GrammarDrillTemplate("今日は料理を作るかわりに、外で夕食を食べましょう。", "Hôm nay thay vì nấu ăn, chúng ta hãy ra ngoài ăn tối nhé.", "かわりに"),
        "〜なんか" to GrammarDrillTemplate("お忙しいところ、私なんかのためにありがとうございます。", "Giữa lúc bận rộn mà anh lại giúp đỡ cho kẻ như tôi, cảm ơn rất nhiều.", "なんか"),
        "〜っぱなし" to GrammarDrillTemplate("部屋を出る時は、電気をつけっぱなしにしないでください。", "Khi ra khỏi phòng, xin đừng để nguyên đèn sáng như thế.", "っぱなし"),

        // Bài 22
        "〜っぽい" to GrammarDrillTemplate("彼はまだ高校生なのに、とても大人っぽい話し方をする。", "Tuy mới là học sinh cấp ba nhưng cậu ấy nói chuyện rất ra dáng người lớn.", "っぽい"),
        "〜きり" to GrammarDrillTemplate("大学を卒業したきり、彼とは一度も会っていない。", "Kể từ khi tốt nghiệp đại học đến giờ, tôi chưa gặp lại anh ấy lần nào.", "きり"),
        "〜ことにしている" to GrammarDrillTemplate("健康のために、毎日野菜を食べることにしています。", "Vì sức khỏe, tôi quyết định duy trì thói quen ăn rau mỗi ngày.", "ことにしています"),
        "〜ことにしています" to GrammarDrillTemplate("健康のために、毎日野菜を食べることにしています。", "Vì sức khỏe, tôi quyết định duy trì thói quen ăn rau mỗi ngày.", "ことにしています"),
        "〜はずだ" to GrammarDrillTemplate("彼は毎日練習していたから、きっと合格するはずだ。", "Vì anh ấy luyện tập mỗi ngày nên chắc chắn sẽ đỗ.", "はずだ"),
        "〜ふりをする" to GrammarDrillTemplate("本当は知っているのに、知らないふりをしている。", "Thực ra biết rõ nhưng lại giả vờ như không biết.", "ふりをしている"),

        // Bài 23
        "〜べきだ" to GrammarDrillTemplate("約束したことは、最後まで必ず守るべきだ。", "Những việc đã hứa thì chắc chắn phải giữ lời đến cùng.", "べきだ"),
        "〜とおり" to GrammarDrillTemplate("説明書に書いてあるとおりに組み立ててください。", "Hãy lắp ráp đúng như những gì viết trong sách hướng dẫn.", "とおり"),
        "〜わけだ" to GrammarDrillTemplate("毎日10時間も勉強したのか。それで合格したわけだ。", "Học tận 10 tiếng mỗi ngày cơ à. Thảo nào mà thi đỗ.", "わけだ"),
        "〜てみる" to GrammarDrillTemplate("難しそうに見えるけれど、一度自分でやってみるといい。", "Trông có vẻ khó đấy nhưng thử tự mình làm một lần xem.", "てみる"),
        "もしかすると〜かもしれない" to GrammarDrillTemplate("空が暗くなってきた。もしかすると雨が降るかもしれない。", "Trời tối sầm lại rồi. Không chừng lát nữa trời sẽ mưa.", "かもしれない"),

        // Bài 24
        "〜っけ？" to GrammarDrillTemplate("明日の会議は何時からだったっけ？", "Cuộc họp ngày mai bắt đầu từ mấy giờ nhỉ?", "っけ"),
        "〜がち" to GrammarDrillTemplate("冬は天気が曇りがちで、洗濯物が乾きにくい。", "Mùa đông trời thường hay âm u nên quần áo khó khô.", "がち"),
        "〜ように" to GrammarDrillTemplate("風邪を引かないように、手洗いうがいをしっかりしましょう。", "Để không bị cảm cúm, hãy nhớ rửa tay và súc họng cẩn thận.", "ように"),
        "〜ようになった" to GrammarDrillTemplate("毎日練習したので、漢字が読めるようになった。", "Vì luyện tập mỗi ngày nên tôi đã bắt đầu đọc được chữ Hán.", "ようになった"),

        // Bài 25
        "〜らしい" to GrammarDrillTemplate("天気予報によると、明日は雪が降るらしい。", "Theo dự báo thời tiết thì nghe nói ngày mai tuyết sẽ rơi.", "らしい"),
        "〜られた" to GrammarDrillTemplate("満員電車の中で、隣の人に足を踏まれた。", "Trong chuyến tàu đông nghẹt, tôi bị người bên cạnh giẫm lên chân.", "れた"),
        "〜ている" to GrammarDrillTemplate("妹は今、リビングで宿題をしている。", "Em gái tôi bây giờ đang làm bài tập ở phòng khách.", "ている"),
        "〜くせに" to GrammarDrillTemplate("自分では何もしないくせに、文句ばかり言っている。", "Bản thân chẳng làm gì cả thế mà lúc nào cũng chỉ phàn nàn.", "くせに"),
        "〜わりには" to GrammarDrillTemplate("このレストランは値段が安いわりには、料理がとても美味しい。", "Nhà hàng này so với giá rẻ thì món ăn rất ngon.", "わりには"),

        // Bài 26
        "〜とく" to GrammarDrillTemplate("お客様が来る前に、部屋をきれいに掃除しとくよ。", "Trước khi khách tới, tôi sẽ dọn dẹp phòng sạch sẽ sẵn.", "とく"),
        "〜ことになっている" to GrammarDrillTemplate("この図書館では、飲食は禁止されることになっている。", "Ở thư viện này theo quy định là cấm ăn uống.", "ことになっている"),
        "〜上げる" to GrammarDrillTemplate("徹夜して、なんとかレポートを書き上げた。", "Thức trắng đêm, cuối cùng tôi cũng viết xong bản báo cáo.", "上げた"),
        "〜切れない" to GrammarDrillTemplate("こんなにたくさんの料理は、一人では食べ切れない。", "Nhiều đồ ăn thế này thì một mình không thể ăn hết nổi.", "切れない"),
        "〜かけ" to GrammarDrillTemplate("机の上に読みかけの本が置いたままになっている。", "Trên bàn vẫn còn để quyển sách đang đọc dở.", "かけ"),

        // Bài 27
        "〜だらけ" to GrammarDrillTemplate("泥だらけの靴で部屋に入ってはいけない。", "Không được đi đôi giày toàn bùn đất vào phòng.", "だらけ"),
        "〜ことは...が" to GrammarDrillTemplate("このパソコンは便利なことは便利だが、値段が高すぎる。", "Chiếc máy tính này tiện thì cũng tiện thật, nhưng giá đắt quá.", "ことは"),
        "〜んだって" to GrammarDrillTemplate("山田さん、来月から大阪に転勤するんだって。", "Nghe nói anh Yamada từ tháng sau sẽ chuyển công tác sang Osaka đấy.", "んだって"),
        "〜んだもん" to GrammarDrillTemplate("だって、約束の時間に遅れたのはそっちのせいなんだもん。", "Tại vì người trễ giờ hẹn là đằng ấy mà.", "んだもん")
    )

    fun findTemplate(grammarTitle: String): GrammarDrillTemplate? {
        templatesByExactTitle[grammarTitle]?.let { return it }
        val normalized = grammarTitle.replace("〜", "").replace("～", "").replace("。", "").trim()
        if (normalized.length < 2) return null
        return templatesByExactTitle.entries.firstOrNull { (k, _) ->
            val cleanKey = k.replace("〜", "").replace("～", "").replace("。", "").trim()
            cleanKey.length >= 2 && (cleanKey == normalized || normalized.contains(cleanKey))
        }?.value
    }

    fun getExamples(grammarTitle: String, cleanGrammar: String, meaning: String): List<GrammarExample> {
        val template = findTemplate(grammarTitle)
        if (template != null) {
            return listOf(GrammarExample(jp = template.jp, vn = template.vn))
        }
        return listOf(
            GrammarExample(
                jp = "大切な場面で、${cleanGrammar}という表現がよく使われます。",
                vn = "Trong tình huống quan trọng, cách diễn đạt mang nghĩa \"$meaning\" rất hay được sử dụng."
            )
        )
    }
}
