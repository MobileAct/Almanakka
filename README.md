# Almanakka

## ToDo

- [x] 文字スタイル
  - [x] 月(ラベル)
  - [x] 平日(ラベル)
  - [x] 土曜(ラベル)
  - [x] 日曜(ラベル)
  - [x] 平日(数値)
    - [x] 通常
    - [x] 非活性時
    - [x] 選択時
    - [x] 前後の月の日付
  - [x] 土曜(数値)
    - [x] 通常
    - [x] 非活性時
    - [x] 選択時
    - [x] 前後の月の日付
  - [x] 日曜(数値)
    - [x] 通常
    - [x] 非活性時
    - [x] 選択時
    - [x] 前後の月の日付
- [x] Padding
  - [x] 平日(数値)
  - [x] 土曜(数値)
  - [x] 日曜(数値)
  - [x] 平日(ラベル)
  - [x] 土曜(ラベル)
  - [x] 日曜(ラベル)
  - [x] 月(ラベル)
  - [x] 月(全体)
- [x] Margin
  - [x] 週(ラベル)
  - [x] 週(数値)
  - [x] 月(ラベル)
- [ ] 選択モード
  - [x] 単体日付選択
  - [x] 範囲日付選択(タップ)
  - [ ] 範囲日付選択(スライド)
- [x] Direction
  - [x] Scroll
  - [x] Pager
- [x] 日曜/月曜始め
- [x] 曜日リソース
- [x] 月リソース
- [x] 当週の前後の月の日付表示
- [x] Sticky(月)
- [x] 動的選択可能範囲指定
- [x] リスナー
- [ ] Dialog
- [ ] README
  - [ ] SetUp
  - [ ] Usage
  - [ ] Option
  
## Style
### Simple Scroll
```xml
    <almanakka.ui.CalendarView
        android:id="@+id/calendar"
        style="@style/Almanakka.Scroll"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:maxDay="2018 5/15"
        app:minDay="2017 2/10" />
```

### Tap Range Selecting Scroll
```xml
    <almanakka.ui.CalendarView
        android:id="@+id/calendar"
        style="@style/Almanakka.Scroll.TapRange"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:maxDay="2018 5/15"
        app:minDay="2017 2/10" />
```

### Slide Range Selecting Scroll
```xml
    <almanakka.ui.CalendarView
        android:id="@+id/calendar"
        style="@style/Almanakka.Scroll.SlideRange"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:maxDay="2018 5/15"
        app:minDay="2017 2/10" />
```

### Pager
```xml
    <almanakka.ui.CalendarView
        android:id="@+id/calendar"
        style="@style/Almanakka.Pager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:maxDay="2018 5/15"
        app:minDay="2017 2/10" />
```
  
## Attribute
### DayStyle
|name|format|description|
|:--|:--|:--|
|dayMargin|dimension||
|dayPadding|dimension||
|weekdayTextAppearance|reference|type: TextAppearance style|
|weekdayDisabledTextAppearance|reference|type: TextAppearance style|
|weekdaySelectedTextAppearance|reference|type: TextAppearance style|
|weekdayOfDifferentMonthTextAppearance|reference|type: TextAppearance style|
|saturdayTextAppearance|reference|type: TextAppearance style|
|saturdayDisabledTextAppearance|reference|type: TextAppearance style|
|saturdaySelectedTextAppearance|reference|type: TextAppearance style|
|saturdayOfDifferentMonthTextAppearance|reference|type: TextAppearance style|
|sundayTextAppearance|reference|type: TextAppearance style|
|sundayDisabledTextAppearance|reference|type: TextAppearance style|
|sundaySelectedTextAppearance|reference|type: TextAppearance style|
|sundayOfDifferentMonthTextAppearance|reference|type: TextAppearance style|

### DayLabelStyle
|name|format|description|
|:--|:--|:--|
|dayLabelMargin|dimension||
|dayLabelPadding|dimension||
|weekdayLabelTextAppearance|reference|type: TextAppearance style|
|saturdayLabelTextAppearance|reference|type: TextAppearance style|
|sundayLabelTextAppearance|reference|type: TextAppearance style|
|dayOfWeekLabels|reference|type: Array of String<br/>resource of label - DayOfWeek, must have 7 items|

### MonthLabelStyle
|name|format|description|
|:--|:--|:--|
|monthLabelMargin|dimension||
|monthLabelPadding|dimension||
|monthLabelTextAppearance|reference|type: TextAppearance style|
|monthLabels|reference|type: Array of String<br/>resource of label - Month, must have 12 items|

### MonthStyle
|name|format|description|
|:--|:--|:--|
|monthPaddingTop|dimension||
|monthPaddingBottom|dimension||
|monthPaddingSide|dimension||

### SelectedStyle
|name|format|description|
|:--|:--|:--|
|selectedElevation|dimension||
|selectedBackground|reference|type: drawable|
|selectedSlider|reference|type: drawable|
|visibleSelectedSlider|boolean||

### StickyStyle
|name|format|description|
|:--|:--|:--|
|isStickyHeader|boolean||
|stickyHeaderBackgroundColor|color||

### Other
|name|format|description|
|:--|:--|:--|
|minDay|string|format: yyyy MM/dd|
|maxDay|string|format: yyyy MM/dd|
|isShowDaysOfDifferentMonth|boolean|if set true, show days of previous or next month in first week and last week|
|dayOfWeekOrderStart|enum|value: sunday, monday, tuesday, wednesday, thursday, friday, saturday|
|selectionProvider|enum|value: normal, tapRange, slideRange|
|mode|enum|value: scroll, pager|

## Customization(Exmaple)

### Change Day Label
`values/strings.xml`:
```xml
<resources>
    <string name="sunday_label">S</string>
    <string name="monday_label">M</string>
    <string name="tuesday_label">T</string>
    <string name="wednesday_label">W</string>
    <string name="thursday_label">T</string>
    <string name="friday_label">F</string>
    <string name="saturday_label">S</string>
</resources>
```

`values/arrays.xml`:
```xml
<resources>
    <array name="dayOfWeeks">
        <item>@string/sunday_label</item>
        <item>@string/monday_label</item>
        <item>@string/tuesday_label</item>
        <item>@string/wednesday_label</item>
        <item>@string/thursday_label</item>
        <item>@string/friday_label</item>
        <item>@string/saturday_label</item>
    </array>
</resources>
```

`layout.xml`:
```xml
app:dayOfWeekLabels="@array/dayOfWeeks"
```

### Change Month Label
`values/strings.xml`:
```xml
<resources>
    <string name="january_label">January %1$d</string>
    <string name="february_label">February %1$d</string>
    <string name="march_label">March %1$d</string>
    <string name="april_label">April %1$d</string>
    <string name="may_label">May %1$d</string>
    <string name="june_label">June %1$d</string>
    <string name="july_label">July %1$d</string>
    <string name="august_label">August %1$d</string>
    <string name="september_label">September %1$d</string>
    <string name="october_label">October %1$d</string>
    <string name="november_label">November %1$d</string>
    <string name="december_label">December %1$d</string>
</resources>
```

`values-ja/strings.xml`:
```xml
<resources>
    <string name="january_label">%1$d年 %2$d月</string>
    <string name="february_label">%1$d年 %2$d月</string>
    <string name="march_label">%1$d年 %2$d月</string>
    <string name="april_label">%1$d年 %2$d月</string>
    <string name="may_label">%1$d年 %2$d月</string>
    <string name="june_label">%1$d年 %2$d月</string>
    <string name="july_label">%1$d年 %2$d月</string>
    <string name="august_label">%1$d年 %2$d月</string>
    <string name="september_label">%1$d年 %2$d月</string>
    <string name="october_label">%1$d年 %2$d月</string>
    <string name="november_label">%1$d年 %2$d月</string>
    <string name="december_label">%1$d年 %2$d月</string>
</resources>
```

Month label resource convert to label using `String.format`.  
string format arguments is under:
- first argument: year, Short type
- second argument: month, Byte type

`values/arrays.xml`:
```xml
<resources>
    <array name="months">
        <item>@string/january_label</item>
        <item>@string/february_label</item>
        <item>@string/march_label</item>
        <item>@string/april_label</item>
        <item>@string/may_label</item>
        <item>@string/june_label</item>
        <item>@string/july_label</item>
        <item>@string/august_label</item>
        <item>@string/september_label</item>
        <item>@string/october_label</item>
        <item>@string/november_label</item>
        <item>@string/december_label</item>
    </array>
</resources>
```

`layout.xml`:
```xml
app:monthLabels="@array/months"
```

## License
This library is under MIT License

### Core
- using [Kotlin Standard Library](https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib), published by [Apache License 2.0](https://github.com/JetBrains/kotlin/tree/master/license)

### UI
- using [Kotlin Standard Library](https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib), published by [Apache License 2.0](https://github.com/JetBrains/kotlin/tree/master/license)
- using [AndroidX](https://github.com/aosp-mirror/platform_frameworks_support), published by [Apache License 2.0](https://github.com/aosp-mirror/platform_frameworks_support/blob/androidx-master-dev/LICENSE.txt)

### App
- using [Kotlin Standard Library](https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib), published by [Apache License 2.0](https://github.com/JetBrains/kotlin/tree/master/license)
- using [AndroidX](https://github.com/aosp-mirror/platform_frameworks_support), published by [Apache License 2.0](https://github.com/aosp-mirror/platform_frameworks_support/blob/androidx-master-dev/LICENSE.txt)
- using [ConstraintLayout](https://android.googlesource.com/platform/frameworks/opt/sherpa/+/refs/heads/studio-master-dev/constraintlayout/), published by [Apache License 2.0](https://android.googlesource.com/platform/frameworks/opt/sherpa/+/refs/heads/studio-master-dev/constraintlayout/src/main/java/android/support/constraint/ConstraintLayout.java)

### Core Test
- using [gson](https://github.com/google/gson), published by [Apache License 2.0](https://github.com/google/gson/blob/master/LICENSE)
- using [JUnit5](https://github.com/junit-team/junit5), published by [Eclipse Public License 2.0](https://github.com/junit-team/junit5/blob/master/LICENSE.md)

### UI Test
- using [AndroidX Test](https://github.com/android/android-test), published by [Apache License 2.0](https://github.com/android/android-test/blob/master/LICENSE)
- using [JUnit4](https://github.com/junit-team/junit4), published by [Eclipse Public License 1.0](https://github.com/junit-team/junit4/blob/master/LICENSE-junit.txt)

### App Test
- using [AndroidX Test](https://github.com/android/android-test), published by [Apache License 2.0](https://github.com/android/android-test/blob/master/LICENSE)
- using [JUnit4](https://github.com/junit-team/junit4), published by [Eclipse Public License 1.0](https://github.com/junit-team/junit4/blob/master/LICENSE-junit.txt)

## Contribute
ToDo: Write

## Other
Author: [@MeilCli](https://github.com/MeilCli)
