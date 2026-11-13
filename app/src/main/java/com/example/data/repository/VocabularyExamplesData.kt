package com.example.data.repository

/**
 * Kho câu ví dụ tiếng Nhật thực tế cho từ vựng JLPT N3 (thay thế hoàn toàn template giả).
 * Với các từ chưa có câu ví dụ biên soạn riêng, hệ thống sẽ trả về null thay vì sinh câu máy móc vô nghĩa.
 */
object VocabularyExamplesData {

    data class VocabExamplePair(
        val jp: String,
        val vn: String
    )

    private val curatedExamples: Map<String, VocabExamplePair> = mapOf(
        // Bài 1 (50 từ cốt lõi N3)
        "相手" to VocabExamplePair("相手の気持ちをよく考えてから話しましょう。", "Hãy suy nghĩ kỹ cảm nhận của đối phương rồi hãy nói."),
        "型" to VocabExamplePair("この服はデザインも型もとてもきれいです。", "Bộ quần áo này cả thiết kế lẫn kiểu dáng đều rất đẹp."),
        "作品" to VocabExamplePair("美術館で有名な画家の作品を見ました。", "Tôi đã xem tác phẩm của họa sĩ nổi tiếng tại bảo tàng mỹ thuật."),
        "存在" to VocabExamplePair("宇宙には地球以外の生命が存在するかもしれません。", "Trong vũ trụ có lẽ tồn tại sự sống ngoài Trái Đất."),
        "現在" to VocabExamplePair("現在、新しいシステムのテストを行っています。", "Hiện tại, chúng tôi đang tiến hành thử nghiệm hệ thống mới."),
        "企業" to VocabExamplePair("多くのIT企業が新しい人材を探している。", "Nhiều doanh nghiệp IT đang tìm kiếm nhân tài mới."),
        "行き" to VocabExamplePair("東京行きの新幹線は五分後に出発します。", "Tàu Shinkansen đi Tokyo sẽ khởi hành sau 5 phút nữa."),
        "事件" to VocabExamplePair("今朝のニュースで大きな事件が報道された。", "Một sự kiện lớn đã được đưa tin trong bản tin sáng nay."),
        "番" to VocabExamplePair("次は私の番なので、少し緊張しています。", "Tiếp theo là lượt của tôi nên tôi hơi hồi hộp một chút."),
        "対策" to VocabExamplePair("台風が来る前に、しっかり対策を立てておこう。", "Trước khi bão đến, hãy chuẩn bị sẵn biện pháp đối phó kỹ càng."),
        "表現" to VocabExamplePair("自分の気持ちを言葉で表現するのは難しい。", "Diễn tả cảm xúc của bản thân bằng lời nói thật khó."),
        "変化" to VocabExamplePair("季節の変化に合わせて服を選びます。", "Tôi chọn trang phục phù hợp với sự thay đổi của các mùa."),
        "記事" to VocabExamplePair("今朝の新聞にとても面白い記事が載っていた。", "Trên tờ báo sáng nay có đăng một bài báo rất thú vị."),
        "中心" to VocabExamplePair("駅を中心に新しい商業施設が並んでいる。", "Các khu thương mại mới nằm san sát quanh trung tâm nhà ga."),
        "行動" to VocabExamplePair("計画を立てたら、すぐに行動に移すことが大切だ。", "Sau khi lập kế hoạch, điều quan trọng là bắt tay vào hành động ngay."),
        "姿" to VocabExamplePair("一生懸命に練習する選手の姿に感動した。", "Tôi đã xúc động trước hình ảnh các tuyển thủ tập luyện hết mình."),
        "期待" to VocabExamplePair("家族の期待に応えるために毎日頑張っています。", "Tôi cố gắng mỗi ngày để đáp lại sự kỳ vọng của gia đình."),
        "関係" to VocabExamplePair("人間関係を大切にする人は信頼されやすい。", "Người coi trọng các mối quan hệ giữa người với người rất dễ được tin cậy."),
        "理由" to VocabExamplePair("遅刻した理由を先生に正直に説明しました。", "Tôi đã giải thích trung thực lý do đi muộn với thầy giáo."),
        "意識" to VocabExamplePair("健康への意識が高まり、運動を始める人が増えた。", "Ý thức về sức khỏe tăng cao, số người bắt đầu tập thể dục cũng tăng lên."),
        "効果" to VocabExamplePair("この薬は頭痛に対してすぐ効果が出ます。", "Loại thuốc này có hiệu quả ngay đối với chứng đau đầu."),
        "様子" to VocabExamplePair("子供たちの楽しそうな様子を見て安心した。", "Nhìn dáng vẻ vui tươi của bọn trẻ, tôi thấy yên tâm."),
        "活動" to VocabExamplePair("週末は地域のボランティア活動に参加しています。", "Cuối tuần tôi tham gia các hoạt động tình nguyện ở địa phương."),
        "言葉" to VocabExamplePair("丁寧な言葉を使うと、相手に良い印象を与えます。", "Dùng từ ngữ lịch sự sẽ tạo ấn tượng tốt cho đối phương."),
        "現実" to VocabExamplePair("夢だけではなく、現実もしっかり見るべきだ。", "Không chỉ mơ ước mà cũng nên nhìn thẳng vào thực tế."),
        "目的" to VocabExamplePair("今回の旅行の目的は温泉に入ることです。", "Mục đích của chuyến du lịch lần này là đi tắm suối nước nóng."),
        "女性" to VocabExamplePair("この職場では多くの女性がリーダーとして活躍している。", "Tại nơi làm việc này, nhiều phụ nữ đang phát huy năng lực ở vị trí lãnh đạo."),
        "印象" to VocabExamplePair("初対面の印象は最初の数秒で決まると言われている。", "Người ta nói ấn tượng lần đầu gặp được quyết định trong vài giây đầu tiên."),
        "感情" to VocabExamplePair("彼はあまり感情を表に出さないタイプです。", "Anh ấy là kiểu người ít bộc lộ cảm xúc ra bên ngoài."),
        "必要" to VocabExamplePair("外国語を学ぶには毎日の復習が必要です。", "Để học ngoại ngữ thì việc ôn tập mỗi ngày là cần thiết."),
        "男性" to VocabExamplePair("背の高い男性が駅の改札の前で待っていた。", "Một người đàn ông cao lớn đang đứng đợi trước cổng soát vé nhà ga."),
        "場合" to VocabExamplePair("雨が降った場合は、運動会は体育館で行います。", "Trong trường hợp trời mưa, hội thao sẽ được tổ chức trong nhà thi đấu."),
        "影響" to VocabExamplePair("インターネットは私たちの生活に大きな影響を与えた。", "Internet đã mang lại ảnh hưởng lớn đến cuộc sống của chúng ta."),
        "状況" to VocabExamplePair("現在の状況を正確に把握する必要があります。", "Chúng ta cần nắm bắt chính xác tình huống hiện tại."),
        "可能性" to VocabExamplePair("努力を続ければ、合格する可能性が高くなります。", "Nếu tiếp tục nỗ lực, khả năng thi đỗ sẽ cao hơn."),
        "結果" to VocabExamplePair("昨日の試験の結果が今日発表されます。", "Kết quả của kỳ thi hôm qua sẽ được công bố vào hôm nay."),
        "方法" to VocabExamplePair("もっと効率の良い勉強方法を探しています。", "Tôi đang tìm phương pháp học tập có hiệu quả cao hơn."),
        "社会" to VocabExamplePair("卒業したら、社会に出て自分の力を試したい。", "Sau khi tốt nghiệp, tôi muốn bước ra xã hội và thử sức mình."),
        "自分" to VocabExamplePair("失敗しても、まず自分のやり方を見直してみよう。", "Dù thất bại, trước hết hãy thử xem xét lại cách làm của bản thân."),
        "情報" to VocabExamplePair("正しい情報を集めてから判断することが重要です。", "Thu thập thông tin chính xác rồi mới phán đoán là điều rất quan trọng."),
        "離れる" to VocabExamplePair("仕事のために家族と離れて暮らしています。", "Vì công việc nên tôi đang sống xa gia đình."),
        "商人" to VocabExamplePair("昔、この町は多くの商人で賑わっていました。", "Ngày xưa, thị trấn này từng tấp nập rất nhiều thương nhân."),
        "横断" to VocabExamplePair("道路を横断するときは必ず左右を確認してください。", "Khi băng qua đường, nhất định hãy quan sát trái phải."),
        "食物" to VocabExamplePair("新鮮な食物をバランスよく食べることが大切です。", "Ăn thực phẩm tươi sống một cách cân bằng là rất quan trọng."),
        "通す" to VocabExamplePair("カーテンを開けて部屋に光を通しましょう。", "Hãy mở rèm cửa để ánh sáng xuyên vào phòng."),
        "失業" to VocabExamplePair("不況の影響で失業する人が増えてしまった。", "Do ảnh hưởng của suy thoái kinh tế, số người thất nghiệp đã tăng lên."),
        "昼食" to VocabExamplePair("正午になったので、同僚と昼食を食べに行きます。", "Đã đến trưa rồi, tôi đi ăn trưa cùng đồng nghiệp đây."),
        "抱く" to VocabExamplePair("母親は赤ちゃんを優しく腕に抱いた。", "Người mẹ nhẹ nhàng ôm em bé vào lòng."),
        "象" to VocabExamplePair("動物園で大きな象を見て子供が大喜びした。", "Thấy con voi lớn ở sở thú, đứa trẻ vô cùng thích thú."),
        "誘う" to VocabExamplePair("週末に友達を映画に誘ってみようと思います。", "Cuối tuần này tôi định thử rủ bạn đi xem phim."),

        // Một số từ N3 phổ biến ở các bài khác
        "遠慮" to VocabExamplePair("遠慮しないで、何でも質問してください。", "Đừng ngại ngùng, hãy hỏi bất cứ điều gì bạn muốn."),
        "偶然" to VocabExamplePair("駅のホームで偶然、高校の同級生に会いました。", "Tôi tình cờ gặp lại bạn cùng lớp cấp ba ở sân ga."),
        "複雑" to VocabExamplePair("このシステムの構造は思ったよりも複雑です。", "Cấu trúc của hệ thống này phức tạp hơn tôi tưởng tượng."),
        "普段" to VocabExamplePair("普段は自炊していますが、忙しい時は外食します。", "Thường ngày tôi tự nấu ăn, nhưng lúc bận thì ăn ngoài."),
        "節約" to VocabExamplePair("電気代を節約するために、こまめにスイッチを切る。", "Để tiết kiệm tiền điện, hãy thường xuyên tắt công tắc."),
        "引き受ける" to VocabExamplePair("誰もやりたがらない仕事を彼が引き受けてくれた。", "Anh ấy đã đứng ra đảm nhận công việc mà không ai muốn làm."),
        "不合格" to VocabExamplePair("準備が足りなくて試験に不合格になってしまった。", "Vì chuẩn bị chưa đủ nên tôi đã trượt kỳ thi."),
        "幸運" to VocabExamplePair("素晴らしい先生に出会えたのは本当に幸運でした。", "Gặp được người thầy tuyệt vời thực sự là điều may mắn."),
        "目立つ" to VocabExamplePair("赤いコートを着ていたので、遠くからでもよく目立った。", "Vì mặc áo khoác đỏ nên đứng từ xa cũng rất nổi bật."),
        "申し込む" to VocabExamplePair("来週の日本語スピーチ大会に申し込む予定です。", "Tôi dự định đăng ký tham gia cuộc thi hùng biện tiếng Nhật tuần tới."),
        "役に立つ" to VocabExamplePair("この辞書は毎日勉強するときにとても役に立ちます。", "Cuốn từ điển này rất có ích khi tôi học mỗi ngày."),
        "間違える" to VocabExamplePair("電車の乗り換え駅を間違えて遅れてしまった。", "Vì nhầm ga chuyển tàu nên tôi đã bị trễ."),
        "見舞う" to VocabExamplePair("入院している友達をお見舞いに行きました。", "Tôi đã đi thăm người bạn đang nằm viện."),
        "戻る" to VocabExamplePair("忘れ物に気づいて、急いで家に戻りました。", "Nhận ra để quên đồ, tôi vội vàng quay trở lại nhà."),
        "許す" to VocabExamplePair("友達の小さなミスを笑顔で許してあげた。", "Tôi đã mỉm cười tha thứ cho lỗi nhỏ của bạn mình."),
        "文脈" to VocabExamplePair("わからない単語があっても、文脈から意味を推測できます。", "Dù có từ chưa biết, bạn vẫn có thể đoán nghĩa dựa vào mạch văn."),
        "文法" to VocabExamplePair("N3の文法をしっかり復習してから問題を解こう。", "Hãy ôn kỹ ngữ pháp N3 rồi mới giải đề.")
    )

    fun getExample(word: String): VocabExamplePair? = curatedExamples[word.trim()]
}
