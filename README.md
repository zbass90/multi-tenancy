1. Tenant


Tenant Resolver 
- Application에 유입될때 해당 요청에서 테넌트 정보를 확인해야함 (예를 들어 HTTP Request, JWT, AMQP Message ...)

Tenant Content
- 특정 요청과 연결된 테넌트에 대한 정보를 찾을 수 있는 위치를 알아야함.
- 시스템의 다른 구성 요소가 현재 프로세의 현재 테넌트가 무엇인지 이해하고 알 수 있도록 해당 정보를 추출하여 요청 처리 전체 기간 동안 엑세스 할 수 있도록 일종의 테넌트 컨텍스트에 저장해야함

Tenant Interceptor
- 테넌트 정보가 담긴 HTTP 요청을 수신하려면 일종의 인터셉터가 필요함
- 확인자를 사용하여 테넌트 정보를 추출한 다음 이를 컨텍스트 내에 저장하므로 작동 방식은 아래와 같다.


테넌트 확인자 인터페이스 TenantResolver : 특히 다른 위치에서 테넌트를 확인하기 위해 구현할 수 있음.

테넌트 컨텍스트 TenantContext
스레드 로컬 객체에 테넌트 식별자를 포함할 스레드 로컬을 사용하여 이 테넌트 컨텍스트 클래스를 생성했으며
여기에는 현재 요청 처리의 일부로 유용한 테넌트를 검색하도록 현재 테넌트를 설정하는 세 가지 메서드가 있음.
요청이 이행되고 마지막으로 인터셉터가 다시 필요할 때 처리가 테넌트를 지우려면 Spring MVC를 사용하고 있으며 Spring MVC는 요청을 가로채기 전에 작업을 수행할 수 있는 핸들러 인터셉터라는 인터페이스를 제공함.
처리 후 TenantInterceptor에서 확인자와 컨텍스를 모두 사용할 수 있으므로 해당 파일내에 테넌트 확인자를 연결함.(preHandle에 tenantID 주입)

스프링이 인식하도록 만든 HttpHeaderTenantResolver를 @Component로 관리
인터셉터를 WebConfig -> interceptor에 등록(addInterceptors)


2. Data Isolation(데이터 격리)

일반적으로 테넌트 간 데이터를 격리하는 방법은 세 가지 주요 전략을 식별할 수 있다.

1) 판별자를 기반으로 데이터를 분할 하는 것.(Partitioned Data) - 데이터를 다루는데 주의해야함
2)  12:45
    https://www.youtube.com/watch?v=pG-NinTx4O4&t=342