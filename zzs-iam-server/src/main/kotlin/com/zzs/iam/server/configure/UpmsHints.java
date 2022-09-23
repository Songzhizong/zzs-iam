package com.zzs.iam.server.configure;

import com.zzs.iam.server.domain.model.authorization.Authentication;
import com.zzs.iam.server.domain.model.authorization.token.AccessTokenDO;
import com.zzs.iam.server.domain.model.authorization.token.RefreshTokenDO;
import com.zzs.iam.server.domain.model.front.MenuDO;
import com.zzs.iam.server.domain.model.front.TerminalDO;
import com.zzs.iam.server.domain.model.log.OperationLogDO;
import com.zzs.iam.server.domain.model.org.*;
import com.zzs.iam.server.domain.model.role.RoleDO;
import com.zzs.iam.server.domain.model.role.RoleMenuRelDO;
import com.zzs.iam.server.domain.model.twostep.TwoStepConfig;
import com.zzs.iam.server.domain.model.user.HistPasswordDO;
import com.zzs.iam.server.domain.model.user.OtherPlatAuthDO;
import com.zzs.iam.server.domain.model.user.UserDO;
import org.springframework.context.annotation.Configuration;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;

/**
 * @author 宋志宗 on 2022/8/17
 */
@NativeHint(
  trigger = PlatformDO.class,
  types = {
    @TypeHint(types = AccessTokenDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = RefreshTokenDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = Authentication.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = AuthClientDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = PlatformDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = PlatformUserDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = RoleDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = RoleMenuRelDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = TenantDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = TenantUserDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = MenuDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = TerminalDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = OtherPlatAuthDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = TwoStepConfig.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = UserDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = HistPasswordDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = OperationLogDO.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
  }
)
@Configuration
public class UpmsHints implements NativeConfiguration {

}
