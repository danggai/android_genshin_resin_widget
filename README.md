# 원신 레진 위젯 / Genshin Resin Widget

![icon.jpg](./assets/icon.jpg?raw=true)

개인 프로젝트로 개발 된 코틀린 언어를 사용한 안드로이드 앱이에요.

[<img src = "./assets/google-play-logo.png" width="150px">](https://play.google.com/store/apps/details?id=danggai.app.parcelwhere)

- 연락처: donggi9313@gmail.com



## 소개

미호요가 제작한 오픈월드 어드벤처 비디오 게임, 원신의 시간당 충전 재화인 레진의 보유량을 위젯으로 실시간 확인 가능하도록 만든 앱이에요.

현재는 레진 뿐만 아니라, 탐험, 선계 주화 등의 정보를 알림으로 받을 수 있고, 자동으로 HoYoLAB 출석체크를 진행할 수 있어요.

1. 이 앱을 정상적으로 사용하기 위해서 uid와 cookie 값이 필요해요.
2. 앱에 해당 값을 입력하고, 위젯을 생성하면 자신의 현재 레진, 최대 충전까지 남은 시간을 알 수 있어요.



## 개발 환경

- [Android Studio Bumblebee](https://developer.android.com/studio/intro)
- [Koltin](https://developer.android.com/kotlin)
- [Coroutine](https://developer.android.com/kotlin/coroutines?hl=ko)
- [Hilt](https://dagger.dev/hilt/)
- [Retrofit2](https://square.github.io/retrofit/)
- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata?hl=ko) (StateFlow로 Refactoring 중)
- Repository Pattern



## Application Version

- minSdkVersion : 23
- targetSdkVersion : 32





## ScreenShots



| ![screenshot_00.png](./assets/screenshot_00.png?raw=true)<br /><center>메인 화면</center> | ![screenshot_01.png](./assets/screenshot_01.png?raw=true)<br /><center>메인 화면</center> | ![screenshot_02.jpg](./assets/screenshot_02.jpg?raw=true)<br /><center>위젯 예시</center> |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
|                                                              |                                                              |                                                              |





## License

```
MIT License
Copyright (c) 2021 danggai <danggai@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
     
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
     
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
