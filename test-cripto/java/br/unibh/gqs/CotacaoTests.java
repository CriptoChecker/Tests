package br.unibh.gqs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import br.unibh.gqs.entidades.Cotacao;
import br.unibh.gqs.entidades.Moeda;
import br.unibh.gqs.persistencia.CotacaoRepository;

/**
 * Classe de testes para a entidade Cotação.
 *  <br>
 * Para rodar, antes sete a seguinte variável de ambiente: -Dspring.config.location=C:/Users/jvsantos/..., criar um arquivo application.properties contendo as seguitnes variáveis:
 * <br>
 * amazon.aws.accesskey=<br>
 * amazon.aws.secretkey=<br>
 * @author jvsantos
 *
 */

@SpringBootTest(classes = {PropertyPlaceholderAutoConfiguration.class, CotacaoTests.DynamoDBConfig.class})
@TestMethodOrder(MethodName.class)
class CotacaoTests {

    private static Logger LOGGER = LoggerFactory.getLogger(CotacaoTests.class);
    private SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
	    
    @Configuration
	@EnableDynamoDBRepositories(basePackageClasses = { CotacaoRepository.class })
	public static class DynamoDBConfig {

		@Value("${amazon.aws.accesskey}")
		private String amazonAWSAccessKey;

		@Value("${amazon.aws.secretkey}")
		private String amazonAWSSecretKey;

		public AWSCredentialsProvider amazonAWSCredentialsProvider() {
			return new AWSStaticCredentialsProvider(amazonAWSCredentials());
		}

		@Bean
		public AWSCredentials amazonAWSCredentials() {
			return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
		}

		@Bean
		public AmazonDynamoDB amazonDynamoDB() {
			return AmazonDynamoDBClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider())
					.withRegion(Regions.US_EAST_1).build();
		}
	}
    
	@Autowired
	private CotacaoRepository repository;

    @Test
    void teste1Criacao() throws ParseException {
		LOGGER.info("Criando objetos...");
		Cotacao c1 = new Cotacao(df.parse("01/03/2021"), "ETH_TESTE", BigDecimal.valueOf(9816.25),	Moeda.BRL.getDescricao());
		Cotacao c2 = new Cotacao(df.parse("02/03/2021"), "ETH_TESTE", BigDecimal.valueOf(9849.48),	Moeda.BRL.getDescricao());
		Cotacao c3 = new Cotacao(df.parse("03/03/2021"), "ETH_TESTE", BigDecimal.valueOf(9925.12),	Moeda.BRL.getDescricao());
		Cotacao c4 = new Cotacao(df.parse("04/03/2021"), "ETH_TESTE", BigDecimal.valueOf(9957.20),	Moeda.BRL.getDescricao());
		Cotacao c5 = new Cotacao(df.parse("05/03/2021"), "ETH_TESTE", BigDecimal.valueOf(10012.78),	Moeda.BRL.getDescricao());
		Cotacao c6 = new Cotacao(df.parse("06/03/2021"), "ETH_TESTE", BigDecimal.valueOf(10684.45),	Moeda.BRL.getDescricao());
		repository.save(c1);
		repository.save(c2);
		repository.save(c3);
		repository.save(c4);
		repository.save(c5);
		repository.save(c6);
		Iterable<Cotacao> lista = repository.findAll();
		assertNotNull(lista.iterator());
		for (Cotacao cotacao : lista) {
			LOGGER.info(cotacao.toString());
		}
		LOGGER.info("Pesquisado um objeto");
		List<Cotacao> result = repository.findByCodigo("ETH_TESTE");
        assertEquals(6, result.size());
		LOGGER.info("Encontrado: {}", result.size());
	}

    @Test
    void teste2Exclusao() throws ParseException {
		LOGGER.info("Excluindo objetos...");
		List<Cotacao> result = repository.findByCodigo("ETH_TESTE");
		for (Cotacao cotacao : result) {
			LOGGER.info("Excluindo Cotacao id = "+cotacao.getId());
			repository.delete(cotacao);
		}
		result = repository.findByCodigo("ETH_TESTE");
        assertEquals(0, result.size());
		LOGGER.info("Exclusão feita com sucesso");
	}
	
	
}