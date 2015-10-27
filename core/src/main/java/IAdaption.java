import java.util.List;
import java.util.Set;

public interface IAdaption<O, C extends O, R, N extends O> {

    C newCompartment(String ct, Object... args);

    C newCompartment(Class<?> c, Object... args);

    R newRole(String rt, Object... args);

    R newRole(Class<?> clazz, Object... args);

    N newNatural(String nt, Object... args);

    N newNatural(Class<?> clazz, Object... args);

    boolean link(String relationshipType, C compartment, R first, R second);

    boolean unlink(String relationshipType, C compartment, R first, R second);

    Set<R> navigateFrom(String relationshipType, C compartment, R role);

    boolean isLinked(String relationshipType, C compartment, R first, R second);

    Object invoke(O player, String method, Object... args);

    Object invoke(O player, C compartment, String method, Object... args);

    Object invoke(O player, R role, C compartment, String method, Object... args);

    Object invoke(O player, String roleType, C compartment, String method, Object... args);

    boolean plays(O player, R role, C compartment);

    boolean transfer(R role, O player);

    boolean migrate(R role, C compartment);

    boolean bind(O player, R role, C compartment);

    boolean unbind(R role);

    O getPlayer(R role);

    C getCompartment(R role);

    List<R> getRoleByCompartment(C compartment);

    List<R> getRoleByPlayer(O player);

    List<R> getPlayedRoles(O player, C compartment);
}
