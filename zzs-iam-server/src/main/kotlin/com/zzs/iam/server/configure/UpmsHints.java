package com.zzs.iam.server.configure;

import com.zzs.iam.server.domain.model.authorization.Authentication;
import com.zzs.iam.server.domain.model.authorization.token.AccessTokenDo;
import com.zzs.iam.server.domain.model.authorization.token.RefreshTokenDo;
import com.zzs.iam.server.domain.model.front.MenuDo;
import com.zzs.iam.server.domain.model.front.TerminalDo;
import com.zzs.iam.server.domain.model.org.*;
import com.zzs.iam.server.domain.model.role.RoleDo;
import com.zzs.iam.server.domain.model.role.RoleMenuRelDo;
import com.zzs.iam.server.domain.model.twostep.TwoStepConfig;
import com.zzs.iam.server.domain.model.user.HistPasswordDo;
import com.zzs.iam.server.domain.model.user.OtherPlatAuthDo;
import com.zzs.iam.server.domain.model.user.UserDo;
import org.springframework.context.annotation.Configuration;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;

/**
 * @author 宋志宗 on 2022/8/17
 */
@NativeHint(
  trigger = PlatformDo.class,
  types = {
    @TypeHint(types = AccessTokenDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = RefreshTokenDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = Authentication.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = AuthClientDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = PlatformDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = PlatformUserDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = RoleDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = RoleMenuRelDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = TenantDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = TenantUserDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = MenuDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = TerminalDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = OtherPlatAuthDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = TwoStepConfig.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = UserDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = HistPasswordDo.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
  }
)
@Configuration
public class UpmsHints implements NativeConfiguration {

}
