package scroll.internal;

import scala.collection.JavaConversions;
import scroll.internal.support.DispatchQuery;
import scroll.internal.support.DispatchQuery$;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Implementation of {@link IAdaption} interface for SCROLL
 * for easier integration in Java-based applications.
 *
 * @see Compartment
 * @see IAdaption
 */
public class ScrollAdaption implements IAdaption<Object, Object, Object, Object> {
    private static final DispatchQuery EMPTY_QUERY = DispatchQuery$.MODULE$.empty();

    private Map<String, Compartment> model = new HashMap<>();

    private Object newObject(String clazz, Object... args)
            throws ClassNotFoundException,
            NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        if (clazz == null || args == null || clazz.isEmpty())
            throw new IllegalArgumentException("No argument should be null or empty!");
        Class<?> c = Class.forName(clazz);
        Class<?>[] parameterTypes = getParameterTypes(args);
        Constructor<?> con = c.getDeclaredConstructor(parameterTypes);
        if (con == null || (!con.isAccessible()))
            throw new IllegalAccessError("Constructor for " + c.getName() + " could not be found!");
        return con.newInstance(args);
    }

    private Object newObject(Class<?> clazz, Object... args)
            throws NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        if (clazz == null || args == null)
            throw new IllegalArgumentException("No argument should be null or empty!");
        Class<?>[] parameterTypes = getParameterTypes(args);
        Constructor<?> con = clazz.getDeclaredConstructor(parameterTypes);
        if (con == null || (!con.isAccessible()))
            throw new IllegalAccessError("Constructor for " + clazz.getName() + " could not be found!");
        return con.newInstance(args);
    }

    private Class<?>[] getParameterTypes(Object... args) {
        if (args == null) throw new IllegalArgumentException("No argument should be null!");
        Class<?>[] p = new Class<?>[args.length];
        for (int i = 0; i <= args.length; i++) {
            p[i] = args[i].getClass();
        }
        return p;
    }

    @Override
    public Object newCompartment(String ct, Object... args) {
        if (ct == null || ct.isEmpty())
            throw new IllegalArgumentException("No Argument should be null or empty!");
        try {
            Object comp = newObject(ct, args);
            if (comp instanceof Compartment)
                model.put(ct, (Compartment) comp);
            return comp;
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object newCompartment(Class<?> c, Object... args) {
        if (c == null)
            throw new IllegalArgumentException("No Argument should be null or empty!");
        try {
            Object comp = newObject(c, args);
            if (comp instanceof Compartment)
                model.put(c.getName(), (Compartment) comp);
            return comp;
        } catch (NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object newRole(String rt, Object... args) {
        if (rt == null || rt.isEmpty())
            throw new IllegalArgumentException("No Argument should be null or empty!");
        try {
            return newObject(rt, args);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object newRole(Class<?> clazz, Object... args) {
        if (clazz == null)
            throw new IllegalArgumentException("No Argument should be null or empty!");
        try {
            return newObject(clazz, args);
        } catch (NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object newNatural(String nt, Object... args) {
        if (nt == null || nt.isEmpty())
            throw new IllegalArgumentException("No Argument should be null or empty!");
        try {
            return newObject(nt, args);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object newNatural(Class<?> clazz, Object... args) {
        if (clazz == null)
            throw new IllegalArgumentException("No Argument should be null or empty!");
        try {
            return newObject(clazz, args);
        } catch (NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean link(String relationshipType, Object compartment, Object first, Object second) {
        // TODO: implement!
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public boolean unlink(String relationshipType, Object compartment, Object first, Object second) {
        // TODO: implement!
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public Set<Object> navigateFrom(String relationshipType, Object compartment, Object role) {
        // TODO: implement!
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public boolean isLinked(String relationshipType, Object compartment, Object first, Object second) {
        // TODO: implement!
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public Object invoke(Object player, String method, Object... args) {
        if (player == null || method == null || method.isEmpty())
            throw new IllegalArgumentException("No Argument should be null or empty!");

        Object c = getCompartment(player);
        if (c == null) throw new RuntimeException("Player '" + player + "' is not contained in any Compartment!");
        return invoke(player, c, method, args);
    }

    @Override
    public Object invoke(Object player, Object compartment, String method, Object... args) {
        if (player == null || method == null || compartment == null || method.isEmpty())
            throw new IllegalArgumentException("No Argument should be null or empty!");
        if (compartment instanceof Compartment) {
            Compartment comp = (Compartment) compartment;
            Compartment.Player<Object> p = comp.newPlayer(player);
            List<Object> l = Arrays.asList(args);
            scala.collection.Iterator<Object> i = JavaConversions.asScalaIterator(l.iterator());
            p.applyDynamic(method, i.toSeq(), EMPTY_QUERY);
        } else {
            throw new IllegalArgumentException("Argument 'compartment' must be of type 'Compartment!'");
        }
        return null;
    }

    @Override
    public Object invoke(Object player, Object role, Object compartment, String method, Object... args) {
        if (player == null || role == null || method == null || compartment == null || method.isEmpty())
            throw new IllegalArgumentException("No Argument should be null or empty!");
        if (!plays(player, role, compartment))
            throw new IllegalArgumentException("The given player does not play the given role in the given compartment!");
        return invoke(player, compartment, method, args);
    }

    @Override
    public Object invoke(Object player, String roleType, Object compartment, String method, Object... args) {
        if (player == null || roleType == null || method == null || compartment == null || roleType.isEmpty() || method.isEmpty())
            throw new IllegalArgumentException("No Argument should be null or empty!");
        List<Object> roles = this.getPlayedRoles(player, compartment);
        for (Object r : roles) {
            if (r.getClass().getName().equals(roleType)) {
                return invoke(player, r, compartment, method, args);
            }
        }
        return null;
    }

    @Override
    public boolean plays(Object player, Object role, Object compartment) {
        if (player == null || compartment == null || role == null)
            throw new IllegalArgumentException("No Argument should be null!");
        if (compartment instanceof Compartment) {
            Compartment comp = (Compartment) compartment;
            Compartment.Player<Object> p = comp.newPlayer(player);
            // TODO: might not work, maybe we have to use class name comparison here
            return comp.plays().getRoles(p, EMPTY_QUERY).contains(role);
        } else {
            throw new IllegalArgumentException("Argument 'compartment' must be of type 'Compartment!'");
        }
    }

    @Override
    public boolean transfer(Object role, Object player) {
        if (player == null || role == null)
            throw new IllegalArgumentException("No Argument should be null!");
        Object sourcePlayer = getPlayer(role);
        Object c = getCompartment(player);
        if (sourcePlayer == null || c == null) return false;
        Compartment comp = (Compartment) c;
        comp.plays().store().removeEdge(sourcePlayer, role);
        comp.plays().store().addVertex(player);
        comp.plays().store().addVertex(role);
        comp.plays().store().addEdge(player, role);
        return true;
    }

    @Override
    public boolean migrate(Object role, Object compartment) {
        if (role == null || compartment == null)
            throw new IllegalArgumentException("No Argument should be null!");

        Object c = getCompartment(role);
        if (c == null) return false;
        Compartment origComp = (Compartment) c;
        Object player = getPlayer(role);
        List<Object> roles = getPlayedRoles(player, origComp);
        if (player == null || roles.isEmpty()) return false;
        roles.stream().forEach(r -> bind(player, r, compartment));
        return true;
    }

    @Override
    public boolean bind(Object player, Object role, Object compartment) {
        if (player == null || role == null || compartment == null)
            throw new IllegalArgumentException("No Argument should be null!");
        if (compartment instanceof Compartment) {
            Compartment comp = (Compartment) compartment;
            Compartment.Player<Object> p = comp.newPlayer(player);
            comp.plays().store().addVertex(p);
            comp.plays().store().addVertex(role);
            comp.plays().store().addEdge(p, role);
            return true;
        } else {
            throw new IllegalArgumentException("Argument 'compartment' must be of type 'Compartment!'");
        }
    }

    @Override
    public boolean unbind(Object role) {
        if (role == null)
            throw new IllegalArgumentException("No Argument should be null!");
        if (model.values().stream().noneMatch(c -> c.plays().containsPlayer(role))) return false;
        model.values().stream().filter(c -> c.plays().containsPlayer(role)).forEach(c -> c.plays().store().removeVertex(role));
        return true;
    }

    @Override
    public Object getPlayer(Object role) {
        if (role == null)
            throw new IllegalArgumentException("No Argument should be null!");
        if (model.values().stream().noneMatch(c -> c.plays().containsPlayer(role)))
            throw new RuntimeException("Role '" + role + "' is not contained in any compartment!");
        for (Compartment comp : model.values()) {
            if (comp.plays().containsPlayer(role)) {
                Compartment.Player<Object> p = comp.newPlayer(role);
                return p.player(EMPTY_QUERY);
            }
        }
        return null;
    }

    @Override
    public Object getCompartment(Object role) {
        if (role == null)
            throw new IllegalArgumentException("No Argument should be null!");
        if (model.values().stream().noneMatch(c -> c.plays().containsPlayer(role)))
            throw new RuntimeException("Role '" + role + "' is not contained in any compartment!");
        for (Compartment comp : model.values()) {
            if (comp.plays().containsPlayer(role)) {
                return comp;
            }
        }
        return null;
    }

    @Override
    public List<Object> getRoleByCompartment(Object compartment) {
        if (compartment == null)
            throw new IllegalArgumentException("No Argument should be null!");
        if (compartment instanceof Compartment) {
            Compartment comp = (Compartment) compartment;
            return JavaConversions.seqAsJavaList(comp.plays().allPlayers());
        } else {
            throw new IllegalArgumentException("Argument 'compartment' must be of type 'Compartment!'");
        }
    }

    @Override
    public List<Object> getRoleByPlayer(Object player) {
        if (player == null)
            throw new IllegalArgumentException("No Argument should be null!");

        if (model.values().stream().noneMatch(c -> c.plays().containsPlayer(player)))
            throw new RuntimeException("Player '" + player + "' is not contained in any compartment!");

        for (Compartment comp : model.values()) {
            if (comp.plays().containsPlayer(player)) {
                return JavaConversions.seqAsJavaList(comp.plays().getRoles(player, EMPTY_QUERY).toSeq());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<Object> getPlayedRoles(Object player, Object compartment) {
        if (player == null || compartment == null)
            throw new IllegalArgumentException("No Argument should be null or empty!");
        if (compartment instanceof Compartment) {
            Compartment comp = (Compartment) compartment;
            return JavaConversions.seqAsJavaList(comp.plays().getRoles(player, EMPTY_QUERY).toSeq());
        } else {
            throw new IllegalArgumentException("Argument 'compartment' must be of type 'Compartment!'");
        }
    }
}
