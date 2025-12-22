package netcentral.server.accessor;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.auth.BCryptPasswordEncoderService;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.auth.SessionCacheAccessor;
import netcentral.server.enums.UserRole;
import netcentral.server.object.Callsign;
import netcentral.server.object.User;
import netcentral.server.object.request.LoginRequest;
import netcentral.server.object.request.RegisterRequest;
import netcentral.server.record.UserRecord;
import netcentral.server.repository.UserRepository;

@Singleton
public class UserAccessor {
    private static final Logger logger = LogManager.getLogger(UserAccessor.class);
    @Inject
    private UserRepository userRepository;
    @Inject
    private CallsignAccessor callsignAccessor;
    @Inject
    private SessionCacheAccessor sessionCacheAccessor;
    @Inject
    private SessionAccessor sessionAccessor;
    @Inject
    private ActivityAccessor activityAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;

    public User get(User loggedInUser, String id) {
        return getUser(loggedInUser, id, false);
    }

    public User getWithPassword(User loggedInUser, String id) {
        return getUser(loggedInUser, id, true);
    }

    private User getUser(User loggedInUser, String id, boolean bPassword) {
        logger.debug(String.format("get(%s) called", id));
        if ((id == null) || (loggedInUser == null)) {
            logger.debug("Id is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "User id not provided");
        }
        Optional<UserRecord> recOpt = userRepository.findById(id);
        if (!recOpt.isPresent()) {
            logger.debug("User not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        UserRecord rec = recOpt.get();
        if (bPassword) {
            return new User(rec.user_id(), rec.username(), rec.password(), UserRole.values()[rec.role()], rec.callsign(), rec.firstName(), rec.lastName());
        }
        return new User(rec.user_id(), rec.username(), null, UserRole.values()[rec.role()], rec.callsign(), rec.firstName(), rec.lastName());
    }

    public User getUserByName(User loggedInUser, String name, boolean bPassword) {
        logger.debug(String.format("getUserByName(%s) called", name));
        if (name == null) {
            logger.debug("Name is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Name not provided");
        }
        UserRecord rec = userRepository.find(name);
        if (rec == null) {
            logger.debug("User not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        if (bPassword) {
            return new User(rec.user_id(), rec.username(), rec.password(), UserRole.values()[rec.role()], rec.callsign(), rec.firstName(), rec.lastName());
        }
        return new User(rec.user_id(), rec.username(), null, UserRole.values()[rec.role()], rec.callsign(), rec.firstName(), rec.lastName());
    }

    public List<User> getAll(User loggedInUser) {
        List<User> ret = new ArrayList<>();

        if (loggedInUser == null) {
            return ret;
        }

        User userFull;
        if (!(loggedInUser.getRole().equals(UserRole.SYSTEM))) {
            userFull = get(loggedInUser, loggedInUser.getId());
        } else {
            userFull = loggedInUser;
        }

        if ((userFull.getRole().equals(UserRole.ADMIN)) || (userFull.getRole().equals(UserRole.SYSADMIN)) || (userFull.getRole().equals(UserRole.SYSTEM))) {
            List<UserRecord> recs = userRepository.findAll();
            if (!recs.isEmpty()) {
                for (UserRecord rec : recs) {
                    ret.add(new User(rec.user_id(), rec.username(), null, UserRole.values()[rec.role()], rec.callsign(), rec.firstName(), rec.lastName()));
                }
            }
        } else {
            ret.add(userFull);
        }
        Collections.sort(ret, new Comparator<User>() {
            @Override
            public int compare(User obj1, User obj2) {
                return obj1.getEmailAddress().compareTo(obj2.getEmailAddress());
            }
        });
        return ret;
    }

    public User create(User loggedInUser, User obj) {
        if (obj == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "User not provided");
        }
        if ((obj.getEmailAddress() == null) || (obj.getEmailAddress().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Username not provided");
        }
        String id = UUID.randomUUID().toString();
        try {
            BCryptPasswordEncoderService bCryptPasswordEncoderService = new BCryptPasswordEncoderService();
            obj.setPassword(bCryptPasswordEncoderService.encode(obj.getPassword()));
        } catch (Exception e) {
            obj.setPassword(null);
        }
        String callsign = null;
        if (obj.getCallsign() != null) {
            callsign = obj.getCallsign().getCallsign();
        }
       
        UserRecord src = new UserRecord(id, obj.getEmailAddress(), obj.getPassword(), callsign, obj.getFirstName(), obj.getLastName(), obj.getRole().ordinal());
        UserRecord rec = userRepository.save(src);
        obj.setId(rec.user_id());
        obj.setPassword(null);
        try {
            activityAccessor.create(loggedInUser, String.format("User %s created", obj.getEmailAddress()));
            changePublisherAccessor.publishUserUpdate(id, "Create");
        } catch (Exception e) {
        }
        return obj;
    }

    public User update(User loggedInUser, String id, User obj) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        // update to get role
        loggedInUser = get(loggedInUser, loggedInUser.getId());

        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "User id not provided");
        }
        if (obj == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "User not provided");
        }
        if ((obj.getId() != null) && (!id.equals(obj.getId()))){
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "User ids to not match");
        }
        if ((obj.getEmailAddress() == null) || (obj.getEmailAddress().isEmpty()) || (obj.getEmailAddress().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid username");
        }
        if (!isAllowedAdmin(loggedInUser, obj.getId())) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not an admin of the owning organization");
        }

        Optional<UserRecord> recOpt = userRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        UserRecord rec = recOpt.get();
        String username = rec.username();
        String password = rec.password();
        if (obj.getEmailAddress() != null) {
            username = obj.getEmailAddress();
        }
        if (obj.getPassword() != null) {
            try {
                BCryptPasswordEncoderService bCryptPasswordEncoderService = new BCryptPasswordEncoderService();
                password = bCryptPasswordEncoderService.encode(obj.getPassword());
            } catch (Exception e) {
            }
        }
        String callsign = null;
        if (obj.getCallsign() != null) {
            callsign = obj.getCallsign().getCallsign();
        }

        UserRecord updatedRec = new UserRecord(rec.user_id(), username, password, callsign, obj.getFirstName(), obj.getLastName(), obj.getRole().ordinal());

        userRepository.update(updatedRec);
        try {
            activityAccessor.create(loggedInUser, String.format("User %s updated", obj.getEmailAddress()));
            changePublisherAccessor.publishUserUpdate(id, "Update");
        } catch (Exception e) {
        }
        return obj;
    }

    private boolean isAllowedAdmin(User user, String targetId) {
        boolean canUpdate = false;
        if ((user.getRole().equals(UserRole.SYSADMIN)) || (user.getRole().equals(UserRole.SYSTEM)) || (user.getRole().equals(UserRole.ADMIN))) {  // allow admin as there is no org
            canUpdate = true;
        } else if (user.getId().equals(targetId)) {
            // same user
            canUpdate = true;
        }
        return canUpdate;
    }

    public void delete(User loggedInUserOrig, String id) {
        if (loggedInUserOrig == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "User id not provided");
        }

        User loggedInUser = loggedInUserOrig;
        try {
            // need to get 
            loggedInUser = get(loggedInUserOrig, loggedInUserOrig.getId());
        } catch (Exception e){
        }

        if (!isAllowedAdmin(loggedInUser, id)) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not an admin");
        }
        Optional<UserRecord> recOpt = userRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        try {
            activityAccessor.create(loggedInUser, String.format("User %s deleted",recOpt.get().username()));
        } catch (Exception e) {
        }
        userRepository.delete(recOpt.get());
        changePublisherAccessor.publishUserUpdate(id, "Delete");
    }

    public User login(LoginRequest obj) {
        User user = validateUser(obj);
        if (user == null) {
            return null;
        }

        try {
            activityAccessor.create(user, String.format("User %s logged in",obj.username()));
        } catch (Exception e) {
        }

        // validate user
        user.setEmailAddress(obj.username());
        // create a token
        user.setAccessToken(sessionCacheAccessor.add(user));
        user.setPassword(null);
        statisticsAccessor.incrementUserLogins();
        changePublisherAccessor.publishUserUpdate(user.getId(), "Login");
        return user;
    }

    private User validateUser(LoginRequest obj) {
        User user = getUserByName(null, obj.username(), true);
        if (user != null) {
            try {
                BCryptPasswordEncoderService bCryptPasswordEncoderService = new BCryptPasswordEncoderService();
                String savedPassword = user.getPassword();
                if (!bCryptPasswordEncoderService.matches(obj.password(), savedPassword)) {
                    // passwords do not match
                    user = null;
                }
            } catch (Exception e) {
                user = null;
            }
        }
        if (user == null) {
            logger.debug("Invalid username or password");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        return user;
    }


    public User logout(User user) {
        if (user == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        sessionCacheAccessor.remove(user);
        user.setAccessToken(null);
        statisticsAccessor.incrementUserLogouts();
        changePublisherAccessor.publishUserUpdate(user.getId(), "Logout");
        return user;
    }

    public User register(RegisterRequest registerRequest) {
        if ((registerRequest.username() == null) || (registerRequest.username().isEmpty()) || (registerRequest.username().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad username");
        }
        if ((registerRequest.password() == null) || (registerRequest.password().isEmpty()) || (registerRequest.password().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad password");
        }
        if ((registerRequest.password2() == null) || (registerRequest.password2().isEmpty()) || (registerRequest.password2().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad password verification");
        }
        if (!registerRequest.password2().equals(registerRequest.password())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        Callsign callsign = null;
        
        try {
            callsign = callsignAccessor.getByCallsign(null, registerRequest.callsign());
        } catch (Exception e) {
        }

        // if no callsign yet, add it
        if ((callsign == null) && (registerRequest.callsign() != null) && (!registerRequest.callsign().isEmpty()) && (!registerRequest.callsign().isBlank())) {
            callsign = new Callsign();
            callsign.setCallsign(registerRequest.callsign());
            try {
                User systemUser = sessionAccessor.getSystemUser();
                callsign = callsignAccessor.create(systemUser, callsign);
            } catch (Exception e) {
                logger.warn(String.format("Could not create callsign %s on registration", callsign), e);
                callsign = null;
            }
        }

        User systemUser = sessionAccessor.getSystemUser();

        User user = new User();
        user.setEmailAddress(registerRequest.username());
        user.setPassword(registerRequest.password());
        user.setFirstName(registerRequest.firstName());
        user.setLastName(registerRequest.lastName());
        user.setCallsign(callsign);
        if (isFirstUser(systemUser)) {
            user.setRole(UserRole.SYSADMIN);
        } else {
            user.setRole(UserRole.USER);
        }

        try {
            user = create(systemUser, user);
            try {
                activityAccessor.create(user, String.format("User %s registered", user.getEmailAddress()));
            } catch (Exception e) {
            }
        } catch (Exception e) {
            user = null;
        }
        if (user == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Cannot create user");
        }
        return user;
    }

    private boolean isFirstUser(User user) {
        boolean isFirst = true;
        try {
            List<User> users = getAll(user);
            if ((users != null) && (!users.isEmpty())) {
                isFirst = false;
            }
        } catch (Exception e) {
            
        }
        return isFirst;
    }

}
