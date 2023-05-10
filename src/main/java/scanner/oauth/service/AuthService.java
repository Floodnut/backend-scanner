package scanner.oauth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import scanner.checklist.entity.CustomRule;
import scanner.checklist.entity.UsedRule;
import scanner.checklist.repository.CheckListRepository;
import scanner.checklist.repository.UsedCheckListRepository;
import scanner.oauth.dto.OAuthDto;
import scanner.oauth.middleware.OAuthRequestFacade;
import scanner.security.dto.Token;
import scanner.user.entity.User;
import scanner.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final CheckListRepository checkListRepository;
	private final UsedCheckListRepository usedCheckListRepository;
	private final OAuthRequestFacade oauthRequestFacade;

	/**
	 * If user registered, just return token.
	 * Else, register user and return token.
	 * @return user Token
	 */
	public Token getUserAccessToken(@NotNull String code) {
		OAuthDto.GithubTokenResponse tokenResponse = oauthRequestFacade.requestGithubOAuthToken(code);
		OAuthDto.GithubUserDetail userDetail = oauthRequestFacade.requestGithubUserDetail(
			tokenResponse.getAccessToken());

		User user = getUserIfExist(userDetail);

		return oauthRequestFacade.createSocialUserToken(user.getUsername());
	}

	/**
	 * @return registered User
	 */
	public User getUserIfExist(OAuthDto.GithubUserDetail userDetail) {
		Optional<User> user = userRepository.findByUsername(userDetail.getLogin());

		if (user.isPresent())
			return user.get();

		User socialUser = userRepository.save(User.addIndividualSocialUser(userDetail));

		initializeUserRules(socialUser);

		return socialUser;
	}

	/**
	 * Initialize UsedRule from CustomRule for new User.
	 */
	private void initializeUserRules(User user) {
		List<CustomRule> originRules = checkListRepository.findAll();
		List<UsedRule> usedRules = new ArrayList<>();

		for (CustomRule originRule : originRules)
			usedRules.add(new UsedRule(originRule, user));

		usedCheckListRepository.saveAll(usedRules);
	}
}
