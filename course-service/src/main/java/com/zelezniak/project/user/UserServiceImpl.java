package com.zelezniak.project.user;


import com.zelezniak.project.author.AuthorService;
import com.zelezniak.project.author.CourseAuthor;
import com.zelezniak.project.exception.CourseException;
import com.zelezniak.project.exception.CustomErrors;
import com.zelezniak.project.role.Role;
import com.zelezniak.project.role.RoleService;
import com.zelezniak.project.student.Student;
import com.zelezniak.project.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final StudentService studentService;
    private final AuthorService authorService;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String ROLE_STUDENT = "ROLE_STUDENT";
    private static final String ROLE_TEACHER = "ROLE_TEACHER";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    @Transactional
    public void createNewUser(UserData user) {
        if (user != null) {
            validateIfUserExistsAndEmailFormat(user.getEmail());
            String role = user.getRole();
            if (role.equals(ROLE_STUDENT)) {
                studentService.saveStudent(newStudent(user));
            } else if (role.equals(ROLE_TEACHER)) {
                authorService.saveAuthor(newAuthor(user));
            }
        }
    }

    private Student newStudent(UserData user) {
        Student studentToSave = buildStudent(user);
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByName(ROLE_STUDENT));
        studentToSave.setRoles(roles);
        return studentToSave;
    }

    private CourseAuthor newAuthor(UserData user) {
        CourseAuthor authorToSave = buildAuthor(user);
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByName(ROLE_STUDENT));
        roles.add(roleService.findByName(ROLE_TEACHER));
        authorToSave.setRoles(roles);
        return authorToSave;
    }

    private CourseAuthor buildAuthor(UserData user) {
        return CourseAuthor
                .CourseAuthorBuilder
                .buildAuthor(user,passwordEncoder);
    }

    private Student buildStudent(UserData user) {
        return Student
                .StudentBuilder
                .buildStudent(user,passwordEncoder);
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CourseAuthor author = authorService.findByEmail(email);

        if (author != null) {return UserDetailsBuilder.buildUserDetails(author);}
        else {
            Student student = studentService.findByEmail(email);
            if (student != null) {return UserDetailsBuilder.buildUserDetails(student);}
            else {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
        }
    }

    private void validateIfUserExistsAndEmailFormat(String email) {
        if (email.matches(EMAIL_PATTERN)) {
            if (studentService.existsByEmail(email) || authorService.existsByEmail(email)) {
                throw new CourseException(CustomErrors.USER_ALREADY_EXISTS);}
        } else {throw new CourseException(CustomErrors.EMAIL_IN_WRONG_FORMAT);}
    }
}