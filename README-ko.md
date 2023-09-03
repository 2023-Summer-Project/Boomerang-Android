![boomerang_banner](https://github.com/2023-Summer-Project/Boomerang-Android/assets/61890844/b4425d15-adb4-465e-88f8-5641fd97077b)
# *부메랑*, 간편한 물건 대여 어플리케이션 [![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/2023-Summer-Project/Boomerang-Android/blob/main/README.md) [![ko](https://img.shields.io/badge/lang-ko-blue.svg)](https://github.com/2023-Summer-Project/Boomerang-Android/blob/main/README-ko.md)
<div align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=Android&logoColor=white">
  <img src="https://img.shields.io/badge/Jetpack Compose-4285F4?style=for-the-badge&logo=Jetpack Compose&logoColor=white">
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=Firebase&logoColor=white">
  <img src="https://img.shields.io/badge/Naver Open SDK-03C75A?style=for-the-badge&logo=Naver&logoColor=white">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white">
</div>

## Overview 🔍
혹시, 정말 필요한 물건이 있지만, 잠깐 필요한 물건이라 새로 구매하기에는 아까워서 고민하신 적 있으신가요? 아니면, 더이상 쓰지 않는 물건이 있지만 버리기엔 아까워서 고민하신 적 있으신가요? **`부메랑`** 앱이 그 
고민을 해결해 드릴게요! **`부메랑`** 앱은 평소 자주 쓰진 않지만 필요한 물건들을 빌리거나 빌려줄 수 있는 작은 온라인 벼룩시장 어플케이션 입니다. **`부메랑`** 앱을 사용하여 쉽게 물건을 빌리고, 빌려주세요!. 
**`부메랑`** 앱은 이런 상황에서 가장 유용하게 쓰실 수 있어요!
> 여름 휴가로 난생 처음 캠핑을 떠나고자 하는 당신, 하지만 비싼 텐트를 바로 구매하기 망설이고 있다면,

> 급하게 식탁을 수리해야 하는데 공구가 없는 당신, 십자 드라이버 한개를 쓰기 위해 비싼 공구 세트를 살까 말까 고민하고 있다면,

**`부메랑`** 앱이 이 고민들을 덜어드릴게요. 또한, 더이상 쓰지 않는 물건들을 빌려줘서 소소한 용돈도 벌어보세요!

## Motivation 💪
대학생인 저희로서 학기 중에는 기숙사나 자취방에서 생활하는 일이 많았습니다. 동시에, 혼자서 생활하면서 매번 필요한 물건들을 비싸게 새로 구매하는 것은 매번 쉬운 결정이 아니였습니다. 예전까지는 이런 경우에 지역 오픈채팅이나 기숙사 그룹채팅을 이용해서 물건을
빌릴 수 있는지 물어보고 빌려쓰는 경우가 잦았는데, 저희는 이것이 그닥 안전한 방법은 아니라고 생각했었습니다. 왜냐하면 빌리는 사람 입장서도 내가 필요한 물건을 그 순간 누군가가 마침 가지고 있고, 빌려줄수 있는 경우가 흔하지 않았고, 빌려주는 사람 입장에서도 물건을
빌려줬을 때, 멀쩡히 다시 돌려줄 것이라는 보장도 없었기 때문입니다. 이러한 단점들이 동네나 주변 커뮤니티에서 물건을 빌려 쓰는 것에 어려움을 주었다고 생각했습니다. 따라서 저희는 이 **`부메랑`** 앱을 통해, 이러한 단점을 보완하고 더욱 안전하고, 간편하며, 편리한
물건 대여 중계 서비스를 제작하고자 하였습니다.

## Application Preview 📱
|  | | |
|:---:|:---:|:---:|
|![boomerang_main](https://github.com/2023-Summer-Project/Boomerang-Android/assets/61890844/6845e1ef-6f80-4f9e-84e6-3c8fbb52d945)|![boomerang_search](https://github.com/2023-Summer-Project/Boomerang-Android/assets/61890844/a19ca1fc-80e7-41aa-b12a-26cc7144cae4)|![boomerang_trans_manage](https://github.com/2023-Summer-Project/Boomerang-Android/assets/61890844/3a203157-cfae-48c3-a2eb-ea4629f8f499)|
|`메인 화면`|`검색 화면`|`거래 관리`|
|![boomerang_product_detail](https://github.com/2023-Summer-Project/Boomerang-Android/assets/61890844/8d9736d1-16eb-4149-9c29-ef612d29d7bc)|![product_detail_rent](https://github.com/2023-Summer-Project/Boomerang-Android/assets/61890844/f7543fcf-c61b-4d3b-85fa-9f8233979083)|![boomerang_message](https://github.com/2023-Summer-Project/Boomerang-Android/assets/61890844/38fd499d-6fa7-4202-9091-51a9bc998b63)|
|`물건 상세 정보 화면`|`물건 대여 요청`|`메세지 화면`|
|![product_registration](https://github.com/2023-Summer-Project/Boomerang-Android/assets/61890844/9f1f51aa-8e00-4b8b-bbbf-da4c7926185e)|![place_select](https://github.com/2023-Summer-Project/Boomerang-Android/assets/61890844/77fc582e-8dc6-4a74-9fcb-e4b7ab092da6)||
|`물건 등록 화면`|`거래 장소 선택 화면`||


## Developers 🙋‍♂️🙋
|![avtDoyoon](https://avatars.githubusercontent.com/u/61890844?v=4)|![avtJeunghun](https://avatars.githubusercontent.com/u/103043741?v=4)|
|:---:|:---:|
|김도윤|이정훈|
|https://github.com/doyoonkim3312|https://github.com/jeungHunLee|
|Android-side|iOS-side|

## Tech Stack 💻
|Layer|Android|iOS|
|:---|:---:|:---:|
|Front-end|`Jetpack Compose`|`Swift UI`|
|Application Detail|`Jetpack Libraries`, `MVVM`, `Dependency Injection`, `Kotlin Coroutines`| `Apple Framework`, `MVVM`, `Combine Framework`|
|Back-end|`Firebase-Auth`, `Firebase-Firestore`, `Firebase-Database`, `Firebase-Storage`|`Firebase-Auth`, `Firebase-Firestore`, `Firebase-Database`, `Firebase-Storage`|
