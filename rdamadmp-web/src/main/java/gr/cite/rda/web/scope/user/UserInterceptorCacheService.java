package gr.cite.rda.web.scope.user;

import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.event.UserCredentialTouchedEvent;
import gr.cite.tools.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
public class UserInterceptorCacheService extends CacheService<UserInterceptorCacheService.UserInterceptorCacheValue> {
    private final ConventionService conventionService;

    public static class UserInterceptorCacheValue {

        public UserInterceptorCacheValue() {
        }

        public UserInterceptorCacheValue(String subjectId, UUID userId) {
            this.subjectId = subjectId;
            this.userId = userId;
        }


        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        private String subjectId;
        private UUID userId;

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }
    }



    @EventListener
    public void handleTenantTouchedEvent(UserCredentialTouchedEvent event) {
        if (!this.conventionService.isNullOrEmpty(event.getSubjectId()))
            this.evict(this.buildKey(event.getSubjectId()));
    }
    @Autowired
    public UserInterceptorCacheService(UserInterceptorCacheOptions options, ConventionService conventionService){
        super(options);
        this.conventionService = conventionService;
    }

    @Override
    protected Class<UserInterceptorCacheValue> valueClass() {
        return UserInterceptorCacheValue.class;
    }

    @Override
    public String keyOf(UserInterceptorCacheValue value) {
        return this.buildKey(value.getSubjectId());
    }


    public String buildKey(String subject) {
        HashMap<String, String> keyParts = new HashMap<>();
        keyParts.put("$subject$", subject);
        return this.generateKey(keyParts);
    }
}
