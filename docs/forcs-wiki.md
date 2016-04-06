# OAuth 2.0   Jwt 인증 기반의 IAM 서버 구현 가이드

## 요구사항 정리

금번 FORCS 과제에서 통합인증센터(IAM)가 처리해야할 사항은, 크게 두가지로 맥락을 잡을 수 있다.

1. 통합인증기능
2. 리소스 접근 제어

### 통합인증기능

각각의 어플리케이션에 대한 Single-sign-on 이 되어야 하며, 각 어플리케이션은 통합인증센터를 통해 리소스 접근에 대한 질의를 요구하게 된다.

정리를 해 본 결과 정확하게는 Single-sign-on 중에서 통합인증 기능을 구축해야 함을 알 수 있다.

어플리케이션 A 의 로그아웃,로그인 상태에 따라 어플리케이션 B,C 가 IAM 으로부터 별도의 POST 리퀘스트를 받아 로그인,아웃 처리를 할 필요는 없으며, 각 어플리케이션은 자원의 이용시마다(DB, 리소스) 통합인증센터로부터 토큰이 유효한지 질의에 대해 응답만 해 줄 수 있으면 된다.

따라서, 전체적인 아키텍쳐는 OpenSSO 가 제공하는 Application Integrating 시나리오 중에서 User Authentication in Applications With Identity Services 를 따른다고 할 수 있다.

[OpenSSO Application Integrating 시나리오](http://www.oracle.com/technetwork/java/app-integration-142732.html)

#### User Authentication in Applications With Identity Services

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/opensso_Integrating.jpg)


### 리소스 접근 제어

리소스 접근 제어에 대해서는, FORCS 는 데이터베이스를 Nosql 인 클라우던트를 사용하고, 리소스 스토리지로는 Bluemix Object Storage(Openstack swift와 동일하다) 를 사용할 예정이다. 이 두가지는 모두 URL REST API 호출 방식와 CRUD (GET,POST,PUT,DELETE) 로 이루어져 있다.
 
이는 OpenSSO 의 Policy Agent 가 수행하는 기능 중에서 URL Policy Agent 의 개념을 이해할 필요가 있으며, URL Policy 의 유저 매핑 방식도 OpenSSO 의 ldap 유저 매핑 방식을 벗어나 , 간단한 RDBA 스키마를 구성하여 URL Policy 와 통합인증 유저를 매핑해 주어야 한다.

이 과정에서 통합인증 유저 정보와 유저 매핑 데이터는 우리가 구축할 IAM 센터에서 데이터를 저장하고 있어야 한다. (FORCS 측에서도 이렇게 원하더라.)

또한, 각 통합인증 유저는 테넌트와 유저아이디로 구분이 되어야 하며, URl Policy 에 의해 접근제어를 받는 대상자는 통합인증 유저일 수도 있지만 E-form service provider 를 사용하는 3Th party 어플리케이션이 대상자가 될 수도 있다.

먼저 OpenSSO 의 인증유저와 URL Policy 매핑 방식을 살펴보자.

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/ldap.png)

다음은 FORCS 요구조건을 수렴한 URL Policy 매핑 방식이다.

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/relation.png)

OpenSSO 의 유저 identity 구분자는 하나의 토큰으로 처리하지만, 위의 그림에서는 유저 identity 를 처리하는 토큰에 tenant, userkey, clientkey 등의 정보가 포함되어야 한다. OpenSSO 방식을 취한다면 유저의 토큰에서부터 시작하여 그 유저의 tenant, clientkey 등을 역추적해야 하지만 JWT 토큰을 사용한다면 신뢰성있는 (JWT Signature validate) 토큰이 인증센터로 도착한다면 토큰 자체에 들어있는 정보를 그대로 신뢰성 있는 자료로 사용할 수 있다.

그림에서 표현된 Policy1, 2...  로 표현 한 것은 Google Api (OAuth 2.0) 에서는 Scope 라고 한다.

예) Google 의 Api scope

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/scopegoogle.png)

API Scope 를 제공하는 서비스사마다 내부적으로 스코프를 컨트롤하는 것은 제각각 다르지만, 인증서버가 스코프를 전달받는 방식은 크게 두가지로 나뉜다.

 - 클라이언트가 Jwt 토큰에 직접 스코프를 넣어서 인증서버로 전달하는 방식

인증 토큰에 스코프를 넣어서 전달하는 것은 사전에 IAM 에 사전에 Policy 가 정의되어 있고, 이 Policy 들이 약어로 함축가능할때 사용이 가능하다. 또한 User Role 등을 기술할때도 쓰인다.

```
예)
Token
{
   .
   .
   scopes: ['address','friends']
}

또는

Token
{
   .
   .
   roles: ['system_admin','pm']
}
```

 - 클라이언트가 인증서버로 인증을 요청할 때 스코프를 파라미터로 전달하는 방식

해당 방식은 Facebook 이나 Twitter 어플리케이션을 사용할 때 흔히 접할 수 있다.

또한 FORCS 가 외부 서비스에 API 를 개방하고자 할 때에도 이 형식을 취하도록 하는 것을 추천한다.

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/facebook-scoped.png)

위 그림은 클라이언트가 스코프를 파라미터로 전달하기에 앞서 사용자로부터 해당 스코프에 사용자를 대신하여 접근해도 되는지를 물어보고 있다.

사용자가 수락을 할 경우 클라이언트는 인증서버로 다음과 같은 형식으로 요청을 한다.

```
Client

app.request(function(){
   .
   .
   clientKey: 해당 자원을 대리 요청하는 3th party 클라이언트
   token: 인증토큰
   scopes: ['basic-info','postwall','refreshtoken']
})
```

1. 해당 자원을 대리 요청하는 3th party 클라이언트가 사용자의 인증토큰과 스코프를 들고 인증서버에 요청을 한다.

2. 인증서버는 두가지 권한을 체크한다.

 - 3th party 클라이언트가 해당 스코프에 접근할 권한이 있는지? (스코프가 별도의 빌링을 필요로 하거나 하는 경우)

 - token 의 사용자가 해당 스코프에 접근할 권한이 있는지?

3. 인증서버는 해당하는 3th party 를 통한 제어요청에 한하여, 해당 사용자의 자원 접근 권한을 저장한다.

4. 추후 3th party 를 통한 요청이 올 시에, 3th party 측에서 전달하는 토큰은 단순한 ClientKey 일수도 있고, Jwt 토큰일수도 있다. 어떤 경우이던 인증서버는 토큰으로부터 어떠한 3th party 인가를 알아내어 알맞은 자원 접근을 수락하여야 한다.

5. 3th party 는 각각의 기본 scopes 를 가지고 있다. 만일 사용자 토큰이 없이 clientKey 만이 인증서버에 도착한다면, 기본 scopes 정책만을 수행하도록 한다.

예) Attlacian 제품군이 3th party 어플리케이션(attlacian connect add-on 이라 한다.) 에게 기본적으로 허용하는 접근 자원들

| Path                                                                  |         | GET   | POST          | PUT           | DELETE        |
|-----------------------------------------------------------------------|---------|-------|---------------|---------------|---------------|
| /rest/api/{version}/attachment22.0.alpha1latest                       |         | READ  | N/A           | N/A           | DELETE        |
| /rest/api/{version}/auditing2latest                                   |         | READ  | WRITE         | WRITE         | N/A           |
| /rest/api/{version}/comment/.+/properties22.0.alpha1latest            |         | READ  | WRITE         | WRITE         | DELETE        |
| /rest/api/{version}/component22.0.alpha1latest                        |         | READ  | PROJECT_ADMIN | PROJECT_ADMIN | PROJECT_ADMIN |
| /rest/api/{version}/configuration2latest                              |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/customFieldOption22.0.alpha1latest                |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/dashboard/.+/items/.+/properties22.0.alpha1latest |         | READ  | WRITE         | WRITE         | DELETE        |
| /rest/api/{version}/field22.0.alpha1latest                            |         | READ  | ADMIN         | N/A           | N/A           |
| /rest/api/{version}/filter22.0.alpha1latest                           |         | READ  | WRITE         | WRITE         | DELETE        |
| /rest/api/{version}/group22.0.alpha1latest                            |         | ADMIN | N/A           | N/A           | N/A           |
| /rest/api/{version}/groups/picker22.0.alpha1latest                    |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/issue22.0.alpha1latest                            |         | READ  | WRITE         | WRITE         | DELETE        |
| /rest/api/{version}/issueLink22.0.alpha1latest                        |         | READ  | WRITE         | N/A           | DELETE        |
| /rest/api/{version}/issueLinkType22.0.alpha1latest                    |         | READ  | ADMIN         | ADMIN         | ADMIN         |
| /rest/api/{version}/issuetype22.0.alpha1latest                        |         | READ  | ADMIN         | ADMIN         | ADMIN         |
| /rest/api/{version}/jql/autocompletedata2latest                       |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/mypermissions22.0.alpha1latest                    |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/myself2latest                                     |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/priority22.0.alpha1latest                         |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/project22.0.alpha1latest                          |         | READ  | PROJECT_ADMIN | PROJECT_ADMIN | PROJECT_ADMIN |
| /rest/api/{version}/project/.+/properties/.+22.0.alpha1latest         |         | N/A   | N/A           | WRITE         | N/A           |
| /rest/api/{version}/resolution22.0.alpha1latest                       |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/role2latest                                       |         | ADMIN | N/A           | N/A           | N/A           |
| /rest/api/{version}/screens22.0.alpha1latest                          |         | ADMIN | ADMIN         | N/A           | N/A           |
| /rest/api/{version}/search22.0.alpha1latest                           |         | READ  | READ          | N/A           | N/A           |
| /rest/api/{version}/serverInfo22.0.alpha1latest                       |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/status22.0.alpha1latest                           |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/statuscategory22.0.alpha1latest                   |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/user22.0.alpha1latest                             |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/user/picker22.0.alpha1latest                      |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/user/properties22.0.alpha1latest                  |         | N/A   | N/A           | WRITE         | DELETE        |
| /rest/api/{version}/version22.0.alpha1latest                          |         | READ  | PROJECT_ADMIN | PROJECT_ADMIN | PROJECT_ADMIN |
| /rest/api/{version}/workflow2latest                                   |         | ADMIN | N/A           | N/A           | N/A           |
| /rest/api/{version}/worklog2latest                                    |         | READ  | N/A           | N/A           | N/A           |
| /rest/api/{version}/worklog/list2latest                               |         | N/A   | READ          | N/A           | N/A           |
| /rest/applinks/{version}/entities1.02.0latest                         | PRIVATE | READ  | N/A           | N/A           | N/A           |
| /rest/applinks/{version}/entitylink1.02.0latest                       | PRIVATE | ADMIN | ADMIN         | ADMIN         | ADMIN         |
| /rest/applinks/{version}/entitylink/primary1.02.0latest               | PRIVATE | ADMIN | ADMIN         | ADMIN         | ADMIN         |
| /rest/applinks/{version}/manifest1.02.0latest                         | PRIVATE | ADMIN | ADMIN         | ADMIN         | ADMIN         |
| /rest/applinks/{version}/type/entity1.02.0latest                      | PRIVATE | ADMIN | ADMIN         | ADMIN         | ADMIN         |
| /rest/atlassian-connect/{version}/addons1latest                       |         | READ  | N/A           | N/A           | N/A           |
| /rest/atlassian-connect/{version}/license1latest                      |         | READ  | N/A           | N/A           | N/A           |
| /secure/attachment                                                    |         | READ  | N/A           | N/A           | N/A           |
| /secure/projectavatar                                                 | PRIVATE | READ  | N/A           | N/A           | N/A           |
| /secure/thumbnail                                                     | PRIVATE | READ  | N/A           | N/A           | N/A           |
| /secure/useravatar                                                    | PRIVATE | READ  | N/A           | N/A           | N/A           |
| /secure/viewavatar                                                    | PRIVATE | READ  | N/A           | N/A           | N/A           |

위의 두가지 경우 모두, URL Policy 들의 묶음을 하나의 scope 약어로 정의하는 과정이 필요하다.

FORCS 도 프로젝트를 진행하면서 추후 이러한 서비스를 계획하고 있다면 이 과정이 필요할 수 있겠다.


### 요구사항 결론

OpenSSO 를 배제하고 제어가능하고 라이트한 요구조건을 수행할 IAM 센터와 IAM 과 통신할 인증모듈을 제작하는 데 있어서 다른 기술을 살펴볼 수 있다.

 - 통합인증 서버

기본적인 통합인증 서버의 로직은 OAuth 2.0 방식을 추천할 수 있다.
추가적으로, 사용자 및 3th party Application 의 CRUD REST API 를 제공해야 한다.

이 외에 잦은 호출에 대한 성능검증과 Fail-over 수행이 가능해야 한다.

 - 리소스 접근 제어

리소스 접근 제어에는 URL Policy 를 CRUD 할 수 있는 REST API 를 제공해야 한다. 
또한 통합인증 유저 및 3th party Application 들과 Policy 간의 매핑 REST 또한 제공해야 한다.

 - DB, Switf Wrapper
 
위의 두가지를 구축하면 간단한 wrapper 구현으로 자동적으로 해결될 수 있다.


## 설계

통합 인증 서버 (IAM) 의 동작 구조는 OAuth 2.0 을 따르도록 한다. OAuth 2.0 을 기술하기 앞서 OAuth 1.0 을 살펴보도록 한다.

### OAuth 1.0a

#### OAuth 1.0a 장점

OAuth 1.0a가 기존의 다른 인증과 구분되는 특징은 크게 두 가지이다. 첫째, API를 인증함에 있어 써드파티 어플리케이션에게 사용자의 비밀번호를 노출하지 않고 인증 할 수 있다는 점. 둘째, 인증(authentication)과 API권한 부여(authorization )를 동시에 할 수 있다는 점이다. OAuth 1.0이 만들어지는 시점에는 써드파티에게 비밀번호를 노출하지 않고 인증하는 방법으로서 이미 Open ID가 있었다. 하지만 Open ID는 API의 권한 부여기능을 기지고 있지 않았고 인증 방법도 OAuth와는 방향이 많이 달랐다.

#### OAuth 1.0a 동작방식

OAuth 1.0a 가 작동하기 위해서는 기본적으로 유저(user), 컨슈머(consumer), 서비스 프로바이더(service provider)가 있어야 한다. OAuth 1.0a 인증을 3-legged OAuth 라고 부르기도 하는데 OAuth는 둘이서 하는 것이 아니라 셋이서 하는 것이라는 말이다. 간단하게는 각각 유저는 트위터 사용자, 컨슈머는 트위터 단말 어플리케이션, 서비스 프로바이더는 트위터 API 서비스 라고 생각하면 쉽다.

![](http://i2.wp.com/earlybird.kr/wp-content/uploads/2013/02/oauth2_triangle2.png?w=624)

새로운 트위터 어플리케이션을 앱스토어에서 다운 받았지만, 아직은 어플리케이션을 신뢰할 수 없는 상황이라고 하자. 사용자는 이 어플리케이션에 아이디와 비밀번호를 저장하면 이 어플리케이션이 또 다른 어떤 짓(몰래 아이디/비밀번호를 수집하는 등)을 할 지 모르기 때문에, 어플리케이션에 비밀번호를 저장하고 싶지 않다. OAuth 1.0은 이 경우 트위터 단말 어플리케이션 (consumer)에게 인증토큰(access token)만을 전달하고 단말 어플리케이션이 인증토큰으로 트위터 API(service provider)를 사용할 수 있도록 해준다.

#### 인증토큰

OAuth 1.0a 인증이 완료가 되면 컨슈머 (예를 들면 트위터 모바일 어플리케이션)은 사용자의 아이디/패스워드를 직접 저장하게 되는 것이 아니라, 인증토큰(access token)을 받게 된다. 이 인증 토큰은 OAuth 2.0에서도 같은 개념으로 사용된다. 인증토큰은 커베로스(Kerberos)의 티켓 개념과 비슷하다라고 할 수 있는데, 아래와 같은 특징을 가진다.

1. 컨슈머가 아이디/패스워드를 가지지 않고 API를 사용할 수 있음
2. 필요한 API에만 제한적으로 접근할 수 있도록 권한 제어 가능
3. 사용자가 서비스 프로바이더의 관리 페이지에서 권한 취소 가능
4. 패스워드 변경 시에도 인증 토큰은 계속 유효함.

### OAuth 2.0

#### OAuth 2.0 플로우

![](https://docs.oracle.com/cd/E39820_01/doc.11121/gateway_docs/content/images/oauth/oauth_web_server_flow.png)

#### OAuth 2.0 spec 구성

현재 OAuth 는 oauth-v2 와 oauth-v2-bearer 라는 2개의 표준이 가장 핵심적인 부분이며 현재 RFC에 등록되기 위한 과정을 밟고 있는 중이다. 대부분 “OAuth 2.0 지원”이라고 하는 서비스 들은 이 2가지 spec을 지원한 다는 것을 의미한다. 이 외에도 SAML, JSON 웹 토큰, MAC 토큰 등의 방식이 있지만 아직 활발히 수정 중 이기 때문에 실제 서비스에서 사용되고 있지는 않다.

#### OAuth 2.0의 버전들

API 서비스를 일찍 시작한 회사들은 기존의 OAuth 1.0a 를 계속 유지하는 경우도 많이 있다. 1.0a와 2.0을 사용하는 알려진 서비스 들은 아래와 같다.

![](http://i0.wp.com/earlybird.kr/wp-content/uploads/2013/02/oauth2_provider.png?w=624)

#### 다양한 인증 방식 (Grant types)

앞에서 OAuth 1.0a가 동작하기 위해서는 사용자, 컨슈머, 서비스 프로바이더가 필요하고 3-legged OAuth 라고 불리우기도 한다고 하였지만, OAuth 2.0은 2-legged 모델 등 다양한 인증 방식을 지원한다. 3-legged 모델의 장점은 최종 사용자 뿐 아니라 개발자가 누구인지도 인증하기 때문에 어떤 어플리케이션이 API를 사용하는지 통계/과금 을 위한 필수적인 정보를 얻을 수 있다는 점이다. user-agent 나 referer 같이 변경될 수 있는 값이 아닌 인증을 통해 확실하게 구분할 수 있기 때문에 개발자가 API를 비정상적으로 호출하고 있다거나 할 때 개발자와 직접 연락하는 등의 조치를 취할 수도 있다. 그럼에도 불구하고 리다이렉트와 같은 OAuth에서 필요로하는 동작이 불가능한 시나리오, 둘이서만 인증하는 시나리오 등을 지원하기 위해 OAuth 2.0은 2-legged 모델도 지원하나, 그래도 OAuth 2.0에서 가장 기본이 되는 것은 3-legged 모델이다.

Client 는 기본적으로 Confidential Client 와 Public Client 로 나뉜다.

 - Confidential 클라이언트는 웹 서버가 API를 호출하는 경우 등과 같이 client 증명서(client_secret)를 안전하게 보관할 수 있는 Client를 의미한다.
 - Public Client는 브라우저기반 어플리케이션이나 모바일 어플리케이션 같이 client 증명서를 안전하게 보관할 수 없는 Client를 의미하는데 이런 경우 redirect_uri 를 통해서 client를 인증한다.

OAuth 2.0이 지원하는 인증방식은 client 종류와 시나리오에 따라 아래 4가지가 있다. 하지만 실제로 Authorization Code Grant와 Implicit Grant를 제외하고는 일반적인 3-legged OAuth 가 아니기 때문에 open API에서는 많이 사용되지 않는다.

1. Authorization Code Grant

웹 서버에서 API를 호출하는 등의 시나리오에서 Confidential Client가 사용하는 방식이다. 서버사이드 코드가 필요한 인증 방식이며 인증 과정에서 client_secret 이 필요하다.
로그인시에 페이지 URL에 response_type=code 라고 넘긴다.

2. Implicit Grant

token과 scope에 대한 스펙 등은 다르지만 OAuth 1.0a과 가장 비슷한 인증방식이다. Public Client인 브라우저 기반의 어플리케이션(Javascript application)이나 모바일 어플리케이션에서 이 방식을 사용하면 된다. Client 증명서를 사용할 필요가 없으며 실제로 OAuth 2.0에서 가장 많이 사용되는 방식이다.
로그인시에 페이지 URL에 response_type=token 라고 넘긴다.

3. Password Credentials Grant

이 방식은 2-legged 방식의 인증이다. Client에 아이디/패스워드를 저장해 놓고 아이디/패스워드로 직접 access token을 받아오는 방식이다. Client 를 믿을 수 없을 때에는 사용하기에 위험하기 때문에 API 서비스의 공식 어플리케이션이나 믿을 수 있는 Client에 한해서만 사용하는 것을 추천한다.
로그인시에 API에 POST로 grant_type=password 라고 넘긴다.

4. Client Credentials Grant

어플리케이션 이 Confidential Client일 때 id와 secret을 가지고 인증하는 방식이다.
로그인시에 API에 POST로 grant_type=client_credentials 라고 넘긴다.

5. Extension

OAuth 2.0은 추가적인 인증방식을 적용할 수 있는 길을 열어놓았다. 이런 과도한 확장성을 메인 에디터인 Eran Hammer는 매우 싫어했다고 한다.

Password Credentials Grant와 Client Credentials Grant는 기본적으로 우리가 생각하는 OAuth 의 프로세스를 따르지 않기 때문에 반드시 인증된 client에만 사용되어야 하며 가능하면 사용하지 않는 것이 좋다.

#### 다양한 토큰 지원(Access token)

OAuth 2.0은 기본적으로 Bearer 토큰, 즉 암호화하지 않은 그냥 토큰을 주고받는 것으로 인증을 한다. 기본적으로 HTTPS 를 사용하기 때문에 토큰을 안전하게 주고받는 것은 HTTPS의 암호화에 의존한다. 또한 복잡한 signature 등을 생성할 필요가 없기 때문에 curl이 API를 호출 할 때 간단하게 Header 에 아래와 같이 한 줄을 같이 보내므로서 API를 테스트해볼 수 있다.

```
Authorization: Bearer
```

또한 OAuth 2.0은 MAC 토큰과 SAML 형식의 토큰을 지원할 수 있지만 현재 MAC 토큰 스펙은 업데이트 되지 않아 기한 만료된 상태이고 SAML 토큰 형식도 아직은 활발하게 수정중이기 때문에 사용할 수 없는 상태이다. 정리하자면, OAuth 2.0은 다양한 토큰 타입을 지원한지만 실질적으로는 Bearer 토큰 타입만 지원한다.

#### Refresh token

클라이언트가 같은 access token을 오래 사용하면 결국은 해킹에 노출될 위험이 높아진다. 그래서 OAuth 2.0에서는 refresh token 이라는 개념을 도입했다. 즉, 인증 토큰(access token)의 만료기간을 가능한 짧게 하고 만료가 되면 refresh token으로 access token을 새로 갱신하는 방법이다. 이 방법은 개발자들 사이에서는 논란이 있는데, 토큰의 상태를 관리해야 해서 개발이 복잡해 질 뿐만 아니라 토큰이 만료되면 다시 로그인 하도록 하는 것이 보안 면에서도 안전하다는 의견이 있기 때문이다.

#### API 권한 제어 (scope)

OAuth 2.0은 써드파티 어플리케이션의 권한을 설정하기 위한 기능이다. scope의 이름이 스펙에 정의되어있지는 않으며 여러 개의 권한을 요청할 때에는 콤마등을 사용해서 로그인 시에 scope를 넘겨주게 된다.
http://example.com/oauth?….&scope=read_article,update_profile

다음은 설계에 있어 또하나의 고려 방안인 OpenSSO 를 살펴보도록 한다.

### OpenSSO

#### OpenSSO 통합인증 처리 플로우

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/opensso1.png)

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/opensso2.png)

#### OpenSSO Policy 구성

 - Rules

 - Subjects

 - Conditions

 - Response Providers

#### Rules

##### Discovery Service (with resource name)
Discovery Service (with resource name) allows administrators to create and manage policies corresponding to the LOOKUP and UPDATE actions that can be performed on the Discovery Service.

 - LOOKUP

Allow: Enables access to the resource defined in the Rule.

Deny: Denies access to the resource defined in the Rule.

 - UPDATE

Allow: Enables access to the resource defined in the Rule.

Deny: Denies access to the resource defined in the Rule.

##### Liberty Personal Profile Service (with resource name)

Liberty Personal Profile Service (with resource name) allows administrators to create and manage policies corresponding to the MODIFY and QUERY actions that can be performed on the Liberty Personal Profile Service.

 - MODIFY

Interact for Value: Invokes the Liberty Alliance Project Interaction protocol for a value on a resource.

Interact for Consent: Invokes the Liberty Alliance Project Interaction protocol for consent on a resource.

Allow: Enables access to the resource defined in the Rule.

Deny: Denies access to the resource defined in the Rule.

 - QUERY

Interact for Value: Invokes the Liberty Alliance Project Interaction protocol for a value on a resource.

Interact for Consent: Invokes the Liberty Alliance Project Interaction protocol for consent on a resource.

Allow: Enables access to the resource defined in the Rule.

Deny: Denies access to the resource defined in the Rule.


##### URL Policy Agent (with resource name)

URL Policy Agent (with resource name) allows administrators to create and manage policies for policy agents that enforce decisions on http/http(s) URLs.

 - GET

Allow: Enables access to the resource defined in the Rule.

Deny: Denies access to the resource defined in the Rule.

 - POST

Allow: Enables access to the resource defined in the Rule.

Deny: Denies access to the resource defined in the Rule.

다음은 인증방식의 토큰 유형으로 고려중인 JWT 를 살펴보도록 한다.

### JWT

출처 : [http://bcho.tistory.com/999](http://bcho.tistory.com/999)

#### Claim기반 토큰의 개념

OAuth에 의해서 발급되는 access_token은 random string으로 토큰 자체에는 특별한 정보를 가지고 있지 않는 일반적인 스트링 형태 이다. 아래는 페이스북에서 발급된 access_token의 형태로 일반적인 문자열 형태임을 확인할 수 있다.

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/jwt4.png)


```
{
  "id":"terry"
  ,"role":["admin","user"]
  ,"company":"pepsi"
}
```


API나 서비스를 제공하는 서버 입장에서 그 access_token을 통해서 사용자에 연관된 권한(예를 들어 scope같은 것)을 식별한 뒤 권한을 허용해주는 구조이다.
즉 서비스를 제공하는 입장에서는 토큰을 가지고 그 토큰과 연관된 정보를 서버쪽에서 찾아야 한다. (사용자 ID나 권한등).
JWT는 Claim 기반이라는 방식을 사용하는데, Claim이라는 사용자에 대한 프로퍼티나 속성을 이야기 한다. 토큰자체가 정보를 가지고 있는 방식인데, JWT는 이 Claim을 JSON을 이용해서 정의한다.
다음은 Claim을 JSON으로 서술한 예이다.이 JSON 자체를 토큰으로 사용하는 것이 Claim 기반의 토큰 방식이다.

이러한 Claim 방식의 토큰은 무엇이 좋을까? 이 토큰을 이용해서 요청을 받는 서버나 서비스 입장에서는 이 서비스를 호출한 사용자에 대한 추가 정보는 이미 토큰안에 다 들어가 있기 때문에 다른 곳에서 가져올 필요가 없다는 것이다.
“사용자 관리” 라는 API 서비스가 있다고 가정하다.
 이 API는 “관리자(admin)” 권한을 가지고 있는 사용자만이 접근이 가능하며, “관리자” 권한을 가지고 있는 사용자는 그 관리자가 속해 있는 “회사(company)”의 사용자 정보만 관리할 수 있다. 라고 정의하자
이  시나리오에 대해서 일반적인 스트링 기반의 토큰과 JWT와 같은 Claim 기반의 토큰이 어떤 차이를 가질 수 있는 지 알아보도록 하자.

#### OAuth 토큰의 경우

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/jwt1.png)

1.    API 클라이언트가 Authorization Server (토큰 발급서버)로 토큰을 요청한다.
이때, 토큰 발급을 요청하는 사용자의 계정과 비밀번호를 넘기고, 이와 함께 토큰의 권한(용도)을 요청한다. 여기서는 일반 사용자 권한(enduser)과 관리자 권한(admin)을 같이 요청하였다.
2.    토큰 생성 요청을 받은 Authorization Server는 사용자 계정을 확인한 후, 이 사용자에게 요청된 권한을 부여해도 되는지 계정 시스템등에 물어본 후, 사용자에게 해당 토큰을 발급이 가능하면 토큰을 발급하고, 토큰에 대한 정보를 내부(토큰 저장소)에 저장해놓는다.
3.    이렇게 생성된 토큰은 API 클라이언트로 저장된다.
4.    API 클라이언트는 API를 호출할때 이 토큰을 이용해서 Resource Server(API 서버)에 있는 API를 호출한다.
5.    이때 호출되는 API는 관리자 권한을 가지고 있어야 사용할 수 있기 때문에, Resource Server가 토큰 저장소에서 토큰에 관련된 사용자 계정, 권한 등의 정보를 가지고 온다. 이 토큰에 (관리자)admin 권한이 부여되어 있기 때문에, API 호출을 허용한다. 위에 정의한 시나리오에서는 그 사용자가 속한 “회사”의 사용자 정보만 조회할 수 있다. 라는 전제 조건을 가지고 있기 때문에, API 서버는 추가로 사용자 데이타 베이스에서 이 사용자가 속한 “회사” 정보를 찾아와야한다.
6.    API서버는 응답을 보낸다.

#### JWT와 같은 Claim 기반의 토큰 흐름

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/jwt2.png)

1.    토큰을 생성 요청하는 방식은 동일하다.  마찬가지로 사용자를 인증한다음에, 토큰을 생성한다.
2.    다른 점은 생성된 토큰에 관련된 정보를 별도로 저장하지 않는다는 것이다. 토큰에 연관되는 사용자 정보나 권한등을 토큰 자체에 넣어서 저장한다.
3.    API를 호출하는 방식도 동일하다.
4.    Resource Server (API 서버)는 토큰 내에 들어 있는 사용자 정보를 가지고 권한 인가 처리를 하고 결과를 리턴한다.
결과적으로 차이점은 토큰을 생성하는 단계에서는 생성된 토큰을 별도로 서버에서 유지할 필요가 없으며
토큰을 사용하는 API 서버 입장에서는 API 요청을 검증하기 위해서 토큰을 가지고 사용자 정보를 별도로 계정 시스템 등에서 조회할 필요가 없다는 것이다.

#### Claim (메세지) 정의

JWT는 Claim을 JSON형태로 표현하는 것인데, JSON은 “\n”등 개행문자가 있기 때문에, REST API 호출시 HTTP Header등에 넣기가 매우 불편하다. 그래서, JWT에서는 이 Claim JSON 문자열을 BASE64 인코딩을 통해서 하나의 문자열로 변환한다.

```
{
  "id":"terry"
  ,"role":["admin","user"]
  ,"company":"pepsi"
}
```

문자열을 BASE64 인코딩 한 결과
ew0KICAiaWQiOiJ0ZXJyeSINCiAgLCJyb2xlIjpbImFkbWluIiwidXNlciJdDQogICwiY29tcGFueSI6InBlcHNpIg0KfQ0K

#### 변조 방지

위의 Claim 기반의 토큰을 봤으면, 첫번째 들 수 있는 의문이 토큰을 받은 다음에 누군가 토큰을 변조해서 사용한다면 어떻게 막느냐? 이다. 이렇게 메세지가 변조 되지 않았음을 증명하는 것을 무결성(Integrity)라고 하는데, 무결성을 보장하는 방법중 많이 사용되는 방법이 서명(Signature)이나 HMAC 사용하는 방식이다. 즉 원본 메세지에서 해쉬값을 추출한 후, 이를 비밀 키를 이용해서 복호화 시켜서 토큰의 뒤에 붙인다. 이게 HMAC방식인데,  누군가 이 메세지를 변조를 했다면,변조된 메세지에서 생성한 해쉬값과 토큰뒤에 붙어 있는 HMAC값이 다르기 때문에 메세지가 변조되었음을 알 수 있다. 다른 누군가가 메세지를 변조한후에, 새롭게 HMAC값을 만들어내려고 하더라도, HAMC은 앞의 비밀키를 이용해서 복호화 되었기 때문에, 이 비밀키를 알 수 없는 이상 HMAC을 만들어 낼 수 없다.

※ HMAC에 대한 자세한 설명은http://bcho.tistory.com/807 를 참고하기 바란다.
그래서 앞의 JSON 메세지에 대해서 SHA-256이라는 알고리즘을 이용해서 비밀키를 “secret” 이라고 하고, HMAC을 생성하면 결과는 다음과 같다.

```
i22mRxfSB5gt0rLbtrogxbKj5aZmpYh7lA82HO1Di0E
```

#### 서명 생성 방식

그러면 무결성 보장을 위해서 사용할 수 있는 알고리즘이 SHA1-256 HMAC 뿐일까? 보안요건에 따라서 SHA1-256,384,512. 그리고 공인 인증서 (Ceritification)을 이용한 RS256 등등 다양한 서명 방식을 지원한다. 그렇다면 JWT 토큰이 어떤 방식으로 서명이 되어 있는지는 어떻게 알 수 있을까?
그래서 JWT 토큰의 맨 앞부분에는 서명에 어떤 알고리즘을 사용했는지를 JSON형태로 정의한후, 이 JSON을 다시 BASE64 방식으로 인코딩한 문자열을 붙인다

```
{"alg":"HS256","typ":"JWT"}
```

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
```

#### 전체 메세지 포맷

위에서 설명한, 서명 방식, JSON 기반의 Claim,그리고 서명(Signature)까지 포함된 전체적인 JWT 토큰의 구조를 보면 다음과 같다.
{서명 방식을 정의한 JSON을 BASE64 인코딩}.{JSON Claim을 BASE64 인코딩}.{JSON Claim에 대한 서명}
이를 정리해서 그림으로 서술해 보면 다음과 같다

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/jwt3.png)

그리고 결과로 나온, JWT 토큰은
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.ew0KICAiaWQiOiJ0ZXJyeSINCiAgLCJyb2xlIjpbImFkbWluIiwidXNlciJdDQogICwiY29tcGFueSI6InBlcHNpIg0KfQ0K.i22mRxfSB5gt0rLbtrogxbKj5aZmpYh7lA82HO1Di0E
가 된다.

#### 문제점

사용이 쉽고, 서버의 개발 부담을 덜어줄 수 있다는 여러가지 장점을 가지고 있으나, 그만큼 또 단점도 가지고 있다.

 - 길이
 
Claim에 넣는 데이터가 많아질 수 록, JWT 토큰의 길이가 길어진다. API 호출등에 사용할 시에,매 호출마다 헤더에 붙어서 가야하기 때문에, 길이가 길다는 것은 그만큼 네트워크 대역폭 낭비가 심하다는 의미이다.

 - 한번 발급된 토큰은 값을 수정하거나 폐기가 불가
 
JWT는 토큰 내에 모든 정보를 다 가지고 있기 때문에, 한번 발급된 토큰에 대한 변경은 서버에서는 더 이상 불가능하다. 예를 들어 토큰을 잘못 발행해서 삭제하고 싶더라도, Signature만 맞으면 맞는 토큰으로 인식을 하기 때문에, 서버에서는 한번 발급된 토큰의 정보를 바꾸는 일등이 불가능하다.
그래서 만약에 JWT를 쓴다면, Expire time을 꼭 명시적으로 두도록 하고, refresh token등을 이용해서, 중간중간 토큰을 재발행하도록 해야 한다. 

### IAM 설계

FORCS IAM 은 OAuth 2.0 의 인증패턴과 OpenSSO 의 URL Policy Driving 의 혼합버전이라 할 수 있다.

단, OpenSSO URL Policy 의 경우 개념을 차용해 새로운 설계가 필요하다.

#### IAM 클라이언트 관리

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/iam.png)

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/iam1.png)

#### IAM 클라이언트 매니지먼트

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/iam2.png)


#### IAM 인증과정 시나리오

##### Trust Client

사용자가 신뢰성 있는 사이트로 접근할 경우, 즉 서비스 제공자 (E-form 서비스) 사이트를 이용할 경우의 인증 시나리오이다. 

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/iam4.png)

##### 3Th party Client

사용자가 외부 어플리케이션을 통해 서비스 제공자의 서비스를 이용할 경우의 인증 시나리오이다.

![](https://github.com/TheOpenCloudEngine/beluga/blob/dev-ops/docs/images/oauth/iam5.png)

##### Resource Direct Access
 
사용자가 리소스로 직접 접근하게 될 경우의 시나리오이다.













