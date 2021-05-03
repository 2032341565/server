package cn.wildfirechat.im.spi.impl.security;

public interface ITokenGenerator {
	public String generateToken(String username);
}
