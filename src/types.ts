export interface ConfigObect {
  attestationHost: string;
  attestationHostCertPinning: string;
  attestationHostReadTimeout?: number;
  attestationRefreshInterval?: number;
  attestationConnectionTimeout?: number;
  googleApiKey: string;
  accessKey: string;
  secretKey: string;
  uniqueId: string;
  developerId: string;
}

export enum TransactionResult {
  TransactionSuccessful = 0,
  TransactionDeclined = 7004,
  TransactionFailed = 7005,
  TransactionNoAppError = 7006,
  TransactionFailedAllowFallback = 7007,
  TransactionCardExpired = 7008,
  TransactionOnlineFail = 7020,
  TransactionCancel = 7024,
  TransactionTimeout = 7028,
  TransactionCardError = 7030,
  TransactionRequireCDCVM = 7054,
  Unknown = 255,
  PresentCard = 71,
  PresentCardTimeout = 74,
  CardPresented = 72,
  CardReadOk = 23,
  CardReadError = 24,
  EnterPin = 65,
  CancelPin = 66,
  PinBypass = 67,
  PinEnterTimeout = 68,
  PinEntered = 69,
  Authorising = 73,
  RequestSignature = 70,
}

export interface TransactionOutcome {
  cardHolderName?: string;
  transactionStatusInfo?: string;
  invoiceNo?: string;
  merchantIdentifier?: string;
  acquirerID?: string;
  transactionId?: string;
  terminalIdentifier?: string;
  statusCode: string;
  approvalCode?: string;
  cardNo?: string;
  transactionID?: string;
  statusMessage: string;
}
