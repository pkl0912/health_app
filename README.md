# health_app

1. MainActivity: 로그인 화면
Materialdesign을 이용하여 id/pwd 작성 부분은 outline box 로 지정했다.
2. HomeActivity: 하단 네비게이션 바
Vector 이미지를 활용해 각 화면을 지칭하는 아이콘으로 지정했다.
3. HomeFragment: 홈 화면
날씨 api를 활용하여 지역을 입력하면 그날의 날씨를 보여주는 이미지가 뜨고,
그 날씨와 어울리는 목표를 랜덤으로 제안하게 하였다.
4. CalendarFragment:: 캘린더 화면
각 날짜를 클릭하면 메모부분을 보여주고, 다시 클릭하면 메모 부분을 숨기는 형식으
로, 토글 형태로 만들었다.
만약 해당 날짜에 메모가 있으면 save 버튼 없이 존재하는 메모가 보이도록 하였고,
메모가 없으면 새로 입력하여 save할 수 있게 만들었다.
5. MyFragment: 프로필 화면
로그인 한 id에 해당하는 사용자 정보를 가져와서 띄울 수 있게 하였고,
편집할 수 있게 만들었다.


